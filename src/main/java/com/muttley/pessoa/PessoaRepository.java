package com.muttley.pessoa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    Optional<Pessoa> findByCpf(String cpf);

    // Busca a pessoa pelo ID carregando o tipo real da hierarquia (Pessoa ou Organizador)
    @Query("SELECT p FROM Pessoa p WHERE p.id = :id")
    Optional<Pessoa> findByIdComTipoReal(@Param("id") Long id);

    @Query(value = "SELECT id FROM pessoas WHERE cpf = :cpf", nativeQuery = true)
    java.util.Optional<Long> findIdByCpf(@Param("cpf") String cpf);

    @Query(value = "SELECT id FROM pessoas WHERE ra = :ra", nativeQuery = true)
    java.util.Optional<Long> findIdByRa(@Param("ra") String ra);
}