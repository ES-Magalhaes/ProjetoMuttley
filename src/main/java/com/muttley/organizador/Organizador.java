package com.muttley.organizador;

import com.muttley.evento.Evento;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organizador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;
	private String disciplina;
	private String qualificacoes;

	@OneToMany(mappedBy = "organizador")
	private List<Evento> eventos;
}