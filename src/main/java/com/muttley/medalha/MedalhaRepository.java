package com.muttley.medalha;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {

       List<Medalha> findByParticipanteId(Long pessoaId);

       boolean existsByParticipanteIdAndNome(Long pessoaId, String nome);

       // Conta quantos eventos com presença confirmada o participante concluiu em uma
       // determinada categoria
       @Query("SELECT COUNT(i) FROM Inscricao i WHERE i.participante.id = :pessoaId AND i.presencaConfirmada = true AND i.evento.categoria = :categoria")
       long countEventosConcluidosPorCategoria(@Param("pessoaId") Long pessoaId, @Param("categoria") String categoria);

       // Soma a carga horária total de eventos concluídos pelo participante
       @Query("SELECT COALESCE(SUM(i.evento.cargaHoraria), 0) FROM Inscricao i WHERE i.participante.id = :pessoaId AND i.presencaConfirmada = true")
       int sumCargaHorariaPorParticipante(@Param("pessoaId") Long pessoaId);
}