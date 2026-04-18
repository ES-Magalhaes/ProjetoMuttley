package com.muttley.evento;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosEvento(Long id,

		@NotBlank(message = "O tema é obrigatório") String tema,

		@NotBlank(message = "A descrição é obrigatória") String descricao,

		@NotBlank(message = "O local do evento é obrigatório") String local,

		@NotNull(message = "A data do evento é obrigatória") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,

		@NotNull(message = "Selecione um organizador") Long organizadorId) {
}
