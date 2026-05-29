package com.muttley.evento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosEvento(
		Long id,

		@NotBlank(message = "O nome do evento é obrigatório") String nome,

		@NotBlank(message = "A descrição é obrigatória") String descricao,

		@NotBlank(message = "A categoria é obrigatória (ex: palestra, workshop)") String categoria,

		@NotBlank(message = "A modalidade é obrigatória (ex: presencial, online)") String modalidade,

		@NotBlank(message = "O local do evento é obrigatório") String local,

		@NotNull(message = "A data do evento é obrigatória") @DateTimeFormat(pattern = "yyyy-MM-dd") @FutureOrPresent(message = "A data do evento deve ser uma data futura.") LocalDate data,

		@NotNull(message = "A hora de início é obrigatória") @DateTimeFormat(pattern = "HH:mm") LocalTime horaInicio,

		@NotNull(message = "A hora de término é obrigatória") @DateTimeFormat(pattern = "HH:mm") LocalTime horaFim,

		@NotNull(message = "A carga horária é obrigatória") @Min(value = 1, message = "A carga horária mínima é de 1 hora") Integer cargaHoraria,

		@NotNull(message = "O número de vagas é obrigatório") @Min(value = 1, message = "O número mínimo de vagas é 1") Integer numeroVagas,

		@NotNull(message = "Selecione um organizador") Long organizadorId,

		// IDs das competências associadas ao evento (opcional)
		List<Long> competenciaIds) {
}