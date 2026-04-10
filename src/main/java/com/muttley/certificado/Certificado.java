package com.muttley.certificado;

import com.muttley.aluno.Aluno;
import com.muttley.evento.Evento;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certificado {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nomeCertificado; // Ex: "Certificado de Participação - Java Conf"
	private Integer pontuacaoObtida;
	private String medalha; // Ouro, Prata, Bronze (conforme seu desenho)

	@ManyToOne
	@JoinColumn(name = "aluno_ra")
	private Aluno aluno;

	@ManyToOne
	@JoinColumn(name = "evento_id")
	private Evento evento;
}
