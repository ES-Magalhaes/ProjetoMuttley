package com.muttley.medalha;

import com.muttley.evento.Evento;
import com.muttley.inscricao.Inscricao;
import com.muttley.organizador.Organizador;
import com.muttley.pessoa.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository medalhaRepository;

    public List<Medalha> listarPorParticipante(Long pessoaId) {
        return medalhaRepository.findByParticipanteId(pessoaId);
    }

    @Transactional
    public Medalha concederMedalhaManual(Medalha medalha) {
        medalha.setTipo("MANUAL");
        return medalhaRepository.save(medalha);
    }

    @Transactional
    public void processarMedalhasCheckIn(Inscricao inscricao) {
        Pessoa participante = inscricao.getParticipante();
        Evento evento = inscricao.getEvento();

        // ---- REGRA 1: Medalha por Categoria (Ex: Concluiu 3 eventos da mesma
        // categoria) ----
        String categoria = evento.getCategoria();
        long qtdPorCategoria = medalhaRepository.countEventosConcluidosPorCategoria(participante.getId(), categoria);

        if (qtdPorCategoria >= 3) {
            String nomeMedalhaCat = "Especialista em " + categoria;
            if (!medalhaRepository.existsByParticipanteIdAndNome(participante.getId(), nomeMedalhaCat)) {
                Medalha m = new Medalha();
                m.setNome(nomeMedalhaCat);
                m.setDescricao("Conquistada ao comparecer a 3 ou mais eventos da categoria " + categoria + ".");
                m.setIcone("bi-award-fill");
                m.setTipo("CATEGORIA");
                m.setParticipante(participante);
                medalhaRepository.save(m);
            }
        }

        // ---- REGRA 2: Medalha por Carga Horária Cumulativa (Ex: Maratonista 20h) ----
        int cargaTotal = medalhaRepository.sumCargaHorariaPorParticipante(participante.getId());
        if (cargaTotal >= 20) {
            String nomeMedalhaHoras = "Maratonista de Conhecimento (20h+)";
            if (!medalhaRepository.existsByParticipanteIdAndNome(participante.getId(), nomeMedalhaHoras)) {
                Medalha m = new Medalha();
                m.setNome(nomeMedalhaHoras);
                m.setDescricao("Conquistada ao acumular mais de 20 horas de carga horária em eventos confirmados.");
                m.setIcone("bi-lightning-charge-fill");
                m.setTipo("CARGA_HORARIA");
                m.setParticipante(participante);
                medalhaRepository.save(m);
            }
        }

        // ---- REGRA 3: Medalha para o Organizador do Evento ----
        Organizador organizador = evento.getOrganizador();
        if (organizador != null) {
            String nomeMedalhaOrg = "Mentor de Comunidade";
            if (!medalhaRepository.existsByParticipanteIdAndNome(organizador.getId(), nomeMedalhaOrg)) {
                Medalha m = new Medalha();
                m.setNome(nomeMedalhaOrg);
                m.setDescricao(
                        "Conquistada por mestres e palestrantes que organizaram ou ministraram um evento institucional.");
                m.setIcone("bi-star-fill");
                m.setTipo("ORGANIZADOR");
                m.setParticipante(organizador); // Polimorfismo aceita o Organizador aqui perfeitamente
                medalhaRepository.save(m);
            }
        }
    }
}