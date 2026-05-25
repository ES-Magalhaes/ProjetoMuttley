package com.muttley.evento;

import java.time.LocalDate;
import java.time.LocalTime; // Importação adicionada

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosEvento(
		Long id,

		@NotBlank(message = "O tema é obrigatório") String tema,

		@NotBlank(message = "A descrição é obrigatória") String descricao,

		@NotBlank(message = "O local do evento é obrigatório") String local,

		@NotNull(message = "A data do evento é obrigatória") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,

		@NotNull(message = "A hora de início é obrigatória") @DateTimeFormat(pattern = "HH:mm") LocalTime horaInicio,

		@NotNull(message = "A hora de término é obrigatória") @DateTimeFormat(pattern = "HH:mm") LocalTime horaFim,

		@NotNull(message = "Selecione um organizador") Long organizadorId) {
}