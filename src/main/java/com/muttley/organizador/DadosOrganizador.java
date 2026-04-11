package com.muttley.organizador;

import jakarta.validation.constraints.NotBlank;

// utilizando o record pois o id do organizador é gerenciado pelo próprio sistema
public record DadosOrganizador (
		Long id,
		
		@NotBlank(message = "O Nome do organizador é obrigatório")
		String nome,
		
		@NotBlank(message = "Informe as disciplinas")
		String disciplinas,
		
		String qualificacoes
		){}
