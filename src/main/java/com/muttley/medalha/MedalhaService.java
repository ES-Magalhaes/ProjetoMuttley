package com.muttley.medalha;

import com.muttley.evento.Evento;
import com.muttley.evento.EventoRepository;
import com.muttley.inscricao.Inscricao;
import com.muttley.inscricao.InscricaoRepository;
import com.muttley.organizador.Organizador;
import com.muttley.pessoa.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository medalhaRepository;

    @Autowired
    private RegraMedalhaRepository regraMedalhaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public List<Medalha> listarPorParticipante(Long pessoaId) {
        return medalhaRepository.findByParticipanteId(pessoaId);
    }

    /**
     * Processa todas as regras de medalha ativas quando um check-in é realizado.
     * Avalia dinamicamente cada regra cadastrada no banco e concede a medalha
     * caso o participante atinja o critério e ainda não possua aquela medalha.
     * Também avalia regras de organizador para o responsável do evento.
     */
    @Transactional
    public void processarMedalhasCheckIn(Inscricao inscricao) {
        Pessoa participante = inscricao.getParticipante();
        Evento evento = inscricao.getEvento();

        // Busca todas as regras ativas cadastradas pelo admin
        List<RegraMedalha> regras = regraMedalhaRepository.findByAtivoTrue();

        for (RegraMedalha regra : regras) {
            if (regra.getTipoCriterio() == TipoCriterio.ORGANIZADOR) {
                // Regras de organizador são avaliadas para o organizador do evento, não o participante
                processarRegraOrganizador(regra, evento);
                continue;
            }

            // Verifica se o participante já possui essa medalha
            if (medalhaRepository.existsByParticipanteIdAndNome(participante.getId(), regra.getNomeMedalha())) {
                continue;
            }

            boolean atingiuCriterio = avaliarCriterio(regra, participante, evento);

            if (atingiuCriterio) {
                concederMedalhaAutomatica(participante, regra);
            }
        }
    }

    /**
     * Avalia regras do tipo ORGANIZADOR para o organizador do evento.
     * Ex: "Organizou pelo menos 1 evento" → ganha medalha "Mentor de Comunidade"
     */
    private void processarRegraOrganizador(RegraMedalha regra, Evento evento) {
        Organizador organizador = evento.getOrganizador();
        if (organizador == null) return;

        // Verifica se já possui essa medalha
        if (medalhaRepository.existsByParticipanteIdAndNome(organizador.getId(), regra.getNomeMedalha())) {
            return;
        }

        // Conta quantos eventos esse organizador tem
        long qtdEventos = eventoRepository.countByOrganizadorId(organizador.getId());
        if (qtdEventos >= regra.getQuantidadeMinima()) {
            Medalha medalha = new Medalha();
            medalha.setNome(regra.getNomeMedalha());
            medalha.setDescricao(regra.getDescricaoMedalha());
            medalha.setIcone(regra.getIcone());
            medalha.setTipo("ORGANIZADOR");
            medalha.setParticipante(organizador);
            medalha.setDataConquista(LocalDateTime.now());
            medalha.setStatus("APROVADA");
            medalhaRepository.save(medalha);
        }
    }

    /**
     * Avalia se o participante atingiu o critério definido na regra.
     */
    private boolean avaliarCriterio(RegraMedalha regra, Pessoa participante, Evento eventoAtual) {
        return switch (regra.getTipoCriterio()) {
            case PARTICIPACOES_CATEGORIA -> {
                long qtd = medalhaRepository.countEventosConcluidosPorCategoria(
                        participante.getId(), regra.getCategoriaEvento());
                yield qtd >= regra.getQuantidadeMinima();
            }
            case PARTICIPACOES_TOTAL -> {
                long totalParticipacoes = inscricaoRepository.countByParticipanteIdAndPresencaConfirmadaTrue(
                        participante.getId());
                yield totalParticipacoes >= regra.getQuantidadeMinima();
            }
            case CARGA_HORARIA -> {
                int cargaTotal = medalhaRepository.sumCargaHorariaPorParticipante(participante.getId());
                yield cargaTotal >= regra.getQuantidadeMinima();
            }
            case EVENTO_ESPECIFICO -> {
                if (regra.getEvento() == null) yield false;
                yield eventoAtual.getId().equals(regra.getEvento().getId());
            }
            case ORGANIZADOR -> {
                // Tratado em processarRegraOrganizador
                yield false;
            }
        };
    }

    /**
     * Concede a medalha ao participante com base na regra atingida.
     */
    private void concederMedalhaAutomatica(Pessoa participante, RegraMedalha regra) {
        Medalha medalha = new Medalha();
        medalha.setNome(regra.getNomeMedalha());
        medalha.setDescricao(regra.getDescricaoMedalha());
        medalha.setIcone(regra.getIcone());
        medalha.setTipo(regra.getTipoCriterio().name());
        medalha.setParticipante(participante);
        medalha.setDataConquista(LocalDateTime.now());
        medalha.setStatus("APROVADA");
        medalhaRepository.save(medalha);
    }
}
