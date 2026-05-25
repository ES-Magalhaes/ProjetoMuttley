package com.muttley.pessoa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    // Método para evitar que o mesmo CPF se inscreva duas vezes
    boolean existsByCPF(String cpf);
}