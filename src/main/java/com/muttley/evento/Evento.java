package com.muttley.evento;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.muttley.aluno.Aluno;
import com.muttley.organizador.Organizador;
import com.muttley.certificado.Certificado;

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
	private Integer pontos; // Referente ao "Contabilizar Pontos" do seu desenho

	@ManyToOne
	@JoinColumn(name = "organizador_id")
	private Organizador organizador;

	@ManyToMany(mappedBy = "eventos")
	private List<Aluno> participantes;

	@OneToMany(mappedBy = "evento")
	private List<Certificado> certificados;
}