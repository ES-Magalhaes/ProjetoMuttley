package com.muttley.inscricao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Transactional
    public void realizarCheckIn(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada."));

        if (inscricao.isPresencaConfirmada()) {
            throw new RuntimeException("O check-in para este participante já foi realizado anteriormente.");
        }

        // Confirma a presença, liberando futuramente o certificado e a medalha
        inscricao.setPresencaConfirmada(true);
        inscricaoRepository.save(inscricao);
    }
}