package com.muttley.medalha;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {

    boolean existsByInscricaoId(Long inscricaoId);

    // Verifica se já existe medalha de um tipo específico para participante + evento
    boolean existsByParticipanteIdAndEventoIdAndTipo(Long pessoaId, Long eventoId, Medalha.TipoMedalha tipo);

    // Verifica se já existe medalha de competência para participante + competência específica
    @Query("SELECT COUNT(m) > 0 FROM Medalha m WHERE m.participante.id = :pessoaId " +
           "AND m.tipo = 'COMPETENCIA' AND m.competenciaNome = :competenciaNome")
    boolean existsMedalhaCompetencia(@Param("pessoaId") Long pessoaId,
                                     @Param("competenciaNome") String competenciaNome);

    List<Medalha> findByParticipanteId(Long pessoaId);

    List<Medalha> findAllByOrderByValidadaAscDataConcessaoDesc();

    List<Medalha> findByValidada(boolean validada);

    // Total de horas acumuladas por participante (soma das cargas horárias dos eventos)
    @Query("SELECT COALESCE(SUM(m.evento.cargaHoraria), 0) FROM Medalha m WHERE m.participante.id = :pessoaId")
    Integer somarHorasPorParticipante(@Param("pessoaId") Long pessoaId);

    long countByParticipanteId(Long pessoaId);

    // Conta quantos eventos com uma competência específica o participante já completou (presença confirmada)
    @Query("SELECT COUNT(DISTINCT i.evento.id) FROM Inscricao i " +
           "JOIN i.evento.competencias c " +
           "WHERE i.participante.id = :pessoaId " +
           "AND i.presencaConfirmada = true " +
           "AND c.id = :competenciaId")
    long contarEventosComCompetencia(@Param("pessoaId") Long pessoaId,
                                     @Param("competenciaId") Long competenciaId);
}
