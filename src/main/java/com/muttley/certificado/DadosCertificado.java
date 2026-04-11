package com.muttley.certificado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCertificado(Long id,

		@NotBlank(message = "O nome do certificado é obrigatório") String nome,

		@NotNull(message = "A pontuação é obrigatória") Integer pontuacao,

		@NotBlank(message = "A medalha é obrigatória") String medalha,

		@NotNull(message = "Selecione um aluno") Long alunoRa,

		@NotNull(message = "Selecione um evento") Long eventoId) {
}