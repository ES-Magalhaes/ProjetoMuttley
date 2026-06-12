package com.muttley.assinante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssinanteRepository extends JpaRepository<Assinante, Long> {
    List<Assinante> findByAtivoTrue();
}
