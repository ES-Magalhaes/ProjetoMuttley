package com.muttley.organizador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosOrganizador(
		Long id,

		@NotBlank(message = "O nome é obrigatório.") @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.") String nome,

		@NotBlank(message = "O e-mail é obrigatório.") @Email(message = "E-mail inválido.") String email,

		@NotBlank(message = "O CPF é obrigatório.") String cpf,

		@NotBlank(message = "O mini currículo é obrigatório.") String miniCurriculo,

		@NotBlank(message = "A formação é obrigatória.") String formacao,

		@NotBlank(message = "A área de atuação é obrigatória.") String areaAtuacao,

		@NotBlank(message = "A instituição é obrigatória.") String instituicao,

		String linkedin,

		String fotoBase64) {
}