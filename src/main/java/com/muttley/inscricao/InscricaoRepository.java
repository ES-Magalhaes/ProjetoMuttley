package com.muttley.inscricao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    // Verifica se o aluno já se inscreveu neste evento específico
    boolean existsByParticipanteIdAndEventoId(Long participanteId, Long eventoId);
}