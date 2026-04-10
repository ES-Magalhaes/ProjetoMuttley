package com.muttley.aluno;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.muttley.evento.Evento;
import com.muttley.certificado.Certificado;

@Entity
@Table(name = "aluno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "ra")
public class Aluno {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aluno_ra")
	private Long ra;

	private String nome;
	private String email;
	private int pontos;

	@OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
	private List<Certificado> certificados;

	@ManyToMany
	@JoinTable(name = "aluno_evento", joinColumns = @JoinColumn(name = "aluno_ra"), inverseJoinColumns = @JoinColumn(name = "evento_id"))
	private List<Evento> eventos;
}