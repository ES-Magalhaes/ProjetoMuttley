package com.muttley.medalha;

import com.muttley.evento.Evento;
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

    public List<Medalha> listarPorParticipante(Long pessoaId) {
        return medalhaRepository.findByParticipanteId(pessoaId);
    }

    @Transactional
    public Medalha concederMedalhaManual(Medalha medalha) {
        medalha.setTipo("MANUAL");
        return medalhaRepository.save(medalha);
    }

    /**
     * Processa todas as regras de medalha ativas quando um check-in é realizado.
     * Avalia dinamicamente cada regra cadastrada no banco e concede a medalha
     * caso o participante atinja o critério e ainda não possua aquela medalha.
     */
    @Transactional
    public void processarMedalhasCheckIn(Inscricao inscricao) {
        Pessoa participante = inscricao.getParticipante();
        Evento evento = inscricao.getEvento();

        // Busca todas as regras ativas cadastradas pelo admin
        List<RegraMedalha> regras = regraMedalhaRepository.findByAtivoTrue();

        for (RegraMedalha regra : regras) {
            // Verifica se o participante já possui essa medalha
            if (medalhaRepository.existsByParticipanteIdAndNome(participante.getId(), regra.getNomeMedalha())) {
                continue;
            }

            boolean atingiuCriterio = avaliarCriterio(regra, participante, evento);

            if (atingiuCriterio) {
                concederMedalhaAutomatica(participante, regra);
            }
        }

        // Regra especial: Medalha para o Organizador do Evento (mantida como lógica fixa)
        processarMedalhaOrganizador(evento);
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
                // Concede medalha se o participante fez check-in neste evento específico
                if (regra.getEvento() == null) yield false;
                yield eventoAtual.getId().equals(regra.getEvento().getId());
            }
            case ORGANIZADOR -> {
                // Regras do tipo ORGANIZADOR são avaliadas separadamente
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
        medalha.setStatus("APROVADA"); // Medalhas automáticas já nascem aprovadas
        medalhaRepository.save(medalha);
    }

    /**
     * Regra fixa: Concede medalha ao organizador do evento quando há check-in.
     */
    private void processarMedalhaOrganizador(Evento evento) {
        Organizador organizador = evento.getOrganizador();
        if (organizador != null) {
            String nomeMedalhaOrg = "Mentor de Comunidade";
            if (!medalhaRepository.existsByParticipanteIdAndNome(organizador.getId(), nomeMedalhaOrg)) {
                Medalha m = new Medalha();
                m.setNome(nomeMedalhaOrg);
                m.setDescricao("Conquistada por mestres e palestrantes que organizaram ou ministraram um evento institucional.");
                m.setIcone("bi-star-fill");
                m.setTipo("ORGANIZADOR");
                m.setParticipante(organizador);
                m.setDataConquista(LocalDateTime.now());
                m.setStatus("APROVADA");
                medalhaRepository.save(m);
            }
        }
    }
}
