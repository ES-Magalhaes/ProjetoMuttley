package com.muttley.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosEvento(Long id,

		@NotBlank(message = "O tema é obrigatório") String tema,

		@NotBlank(message = "A descrição é obrigatória") String descricao,

		@NotNull(message = "A pontuação é obrigatória") Integer pontos,

		@NotNull(message = "Selecione um organizador") Long organizadorId) {
}
