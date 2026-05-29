package com.muttley.medalha;

import com.muttley.competencia.Competencia;
import com.muttley.evento.Evento;
import com.muttley.inscricao.Inscricao;
import com.muttley.organizador.Organizador;
import com.muttley.pessoa.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository medalhaRepository;

    private static final int EVENTOS_PARA_COMPETENCIA = 3;
    private static final int CARGA_HORARIA_CONCLUSAO = 8;

    // =========================================================================
    // CONCESSÃO AUTOMÁTICA DE MEDALHAS (chamado no check-in)
    // =========================================================================

    /**
     * Processa todas as medalhas aplicáveis após um check-in confirmado.
     * Regras:
     * - Organizador/Palestrante do evento NÃO recebe medalha de participação/conclusão
     * - Se carga horária >= 8h → CONCLUSAO (substitui PARTICIPACAO, não gera ambas)
     * - Se carga horária < 8h → PARTICIPACAO
     * - Organizador do evento recebe APRESENTACAO (Palestra/Seminário) ou ORGANIZACAO (demais)
     * - Medalha de COMPETENCIA: após 3 eventos com a mesma competência
     */
    @Transactional
    public void processarMedalhasCheckIn(Inscricao inscricao) {
        Pessoa participante = inscricao.getParticipante();
        Evento evento = inscricao.getEvento();

        boolean ehOrganizadorDoEvento = isOrganizadorDoEvento(participante, evento);

        // 1. Medalha de Participação ou Conclusão (apenas para quem NÃO é organizador do evento)
        if (!ehOrganizadorDoEvento) {
            concederMedalhaParticipacaoOuConclusao(inscricao, participante, evento);
        }

        // 2. Medalha de Organização/Apresentação para o organizador do evento
        //    (concedida na primeira vez que alguém faz check-in, confirmando que o evento aconteceu)
        concederMedalhaOrganizador(evento);

        // 3. Medalhas de Competência (independente do tipo de medalha anterior)
        verificarMedalhasCompetencia(participante, evento);
    }

    private boolean isOrganizadorDoEvento(Pessoa participante, Evento evento) {
        if (evento.getOrganizador() == null) return false;
        return evento.getOrganizador().getId().equals(participante.getId());
    }

    private void concederMedalhaParticipacaoOuConclusao(Inscricao inscricao, Pessoa participante, Evento evento) {
        // Evita duplicata para esta inscrição
        if (medalhaRepository.existsByInscricaoId(inscricao.getId())) {
            return;
        }

        // Se carga horária >= 8h → CONCLUSAO, senão → PARTICIPACAO
        Medalha.TipoMedalha tipo;
        if (evento.getCargaHoraria() != null && evento.getCargaHoraria() >= CARGA_HORARIA_CONCLUSAO) {
            tipo = Medalha.TipoMedalha.CONCLUSAO;
        } else {
            tipo = Medalha.TipoMedalha.PARTICIPACAO;
        }

        Medalha medalha = new Medalha();
        medalha.setParticipante(participante);
        medalha.setEvento(evento);
        medalha.setTipo(tipo);
        medalha.setDataConcessao(LocalDate.now());
        medalha.setInscricaoId(inscricao.getId());
        medalhaRepository.save(medalha);
    }

    private void concederMedalhaOrganizador(Evento evento) {
        Organizador organizador = evento.getOrganizador();
        if (organizador == null) return;

        // Determina o tipo: APRESENTACAO para Palestra/Seminário, ORGANIZACAO para o resto
        String categoria = evento.getCategoria() != null ? evento.getCategoria().toLowerCase() : "";
        Medalha.TipoMedalha tipo;
        if (categoria.contains("palestra") || categoria.contains("seminário") || categoria.contains("seminario")) {
            tipo = Medalha.TipoMedalha.APRESENTACAO;
        } else {
            tipo = Medalha.TipoMedalha.ORGANIZACAO;
        }

        // Evita duplicata: verifica se já existe medalha desse tipo para este organizador neste evento
        if (medalhaRepository.existsByParticipanteIdAndEventoIdAndTipo(
                organizador.getId(), evento.getId(), tipo)) {
            return;
        }

        Medalha medalha = new Medalha();
        medalha.setParticipante(organizador);
        medalha.setEvento(evento);
        medalha.setTipo(tipo);
        medalha.setDataConcessao(LocalDate.now());
        // Não usa inscricaoId pois o organizador não tem inscrição
        medalhaRepository.save(medalha);
    }

    private void verificarMedalhasCompetencia(Pessoa participante, Evento evento) {
        List<Competencia> competencias = evento.getCompetencias();
        if (competencias == null || competencias.isEmpty()) return;

        for (Competencia competencia : competencias) {
            // Já tem medalha de competência para esta competência?
            if (medalhaRepository.existsMedalhaCompetencia(participante.getId(), competencia.getNome())) {
                continue;
            }

            // Conta quantos eventos com esta competência o participante já completou
            long count = medalhaRepository.contarEventosComCompetencia(
                    participante.getId(), competencia.getId());

            if (count >= EVENTOS_PARA_COMPETENCIA) {
                Medalha medalha = new Medalha();
                medalha.setParticipante(participante);
                medalha.setEvento(evento); // Evento que disparou a conquista
                medalha.setTipo(Medalha.TipoMedalha.COMPETENCIA);
                medalha.setCompetenciaNome(competencia.getNome());
                medalha.setDataConcessao(LocalDate.now());
                medalhaRepository.save(medalha);
            }
        }
    }

    // =========================================================================
    // CONSULTAS
    // =========================================================================

    public List<Medalha> listarPorParticipante(Long pessoaId) {
        return medalhaRepository.findByParticipanteId(pessoaId);
    }

    public List<Medalha> listarTodasOrdenadas() {
        return medalhaRepository.findAllByOrderByValidadaAscDataConcessaoDesc();
    }

    public List<Medalha> listarPendentes() {
        return medalhaRepository.findByValidada(false);
    }

    public int totalHorasPorParticipante(Long pessoaId) {
        Integer total = medalhaRepository.somarHorasPorParticipante(pessoaId);
        return total != null ? total : 0;
    }

    public long totalMedalhasPorParticipante(Long pessoaId) {
        return medalhaRepository.countByParticipanteId(pessoaId);
    }

    // =========================================================================
    // VALIDAÇÃO MANUAL (tela /medalha)
    // =========================================================================

    @Transactional
    public void validar(Long medalhaId, String observacao) {
        Medalha medalha = medalhaRepository.findById(medalhaId)
                .orElseThrow(() -> new RuntimeException("Medalha não encontrada."));
        medalha.setValidada(true);
        medalha.setObservacaoValidacao(observacao);
        medalhaRepository.save(medalha);
    }

    @Transactional
    public void revogar(Long medalhaId, String observacao) {
        Medalha medalha = medalhaRepository.findById(medalhaId)
                .orElseThrow(() -> new RuntimeException("Medalha não encontrada."));
        medalha.setValidada(false);
        medalha.setObservacaoValidacao(observacao);
        medalhaRepository.save(medalha);
    }

    @Transactional
    public void excluir(Long medalhaId) {
        medalhaRepository.deleteById(medalhaId);
    }
}
