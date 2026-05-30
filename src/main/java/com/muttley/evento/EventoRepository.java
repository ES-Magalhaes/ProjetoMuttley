package com.muttley.evento;

import com.muttley.competencia.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByOrganizadorId(Long organizadorId);
    List<Evento> findByCompetenciasContaining(Competencia competencia);
}