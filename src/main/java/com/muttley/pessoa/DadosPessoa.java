package com.muttley.pessoa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record DadosPessoa(
        Long id,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
         String cpf,

        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido") String email) {

}
