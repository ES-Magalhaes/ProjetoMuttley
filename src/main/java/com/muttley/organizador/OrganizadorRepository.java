package com.muttley.organizador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {
}