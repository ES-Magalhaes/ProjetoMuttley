package com.muttley.organizador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record DadosOrganizador(
		Long id,

		@NotBlank(message = "O Nome do organizador é obrigatório") String nome,

		@NotBlank(message = "O e-mail é obrigatório") @Email(message = "Informe um e-mail válido") String email,

		@NotBlank(message = "O CPF é obrigatório") @CPF(message = "Informe um CPF válido") String cpf,

		String qualificacoes) {
}