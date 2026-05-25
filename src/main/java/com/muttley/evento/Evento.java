package com.muttley.evento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.muttley.pessoa.Pessoa;

import org.springframework.format.annotation.DateTimeFormat;
import com.muttley.organizador.Organizador;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String tema;
	private String descricao;
	private String local;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate data;

	// Novos atributos de hora
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime horaInicio;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime horaFim;

	@ManyToOne
	@JoinColumn(name = "organizador_id")
	private Organizador organizador;

	@ManyToMany(mappedBy = "eventosInscritos")
	private List<Pessoa> participantes;
}