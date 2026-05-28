package com.muttley.certificado.repository;

import com.muttley.certificado.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    boolean existsByInscricaoId(Long inscricaoId);
}
