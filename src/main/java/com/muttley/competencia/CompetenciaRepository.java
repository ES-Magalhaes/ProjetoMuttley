package com.muttley.competencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {
    List<Competencia> findByTipo(Competencia.TipoCompetencia tipo);
}
