package com.muttley.evento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.muttley.aluno.Aluno;
import com.muttley.certificado.Certificado;
import com.muttley.organizador.Organizador;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@ManyToOne
	@JoinColumn(name = "organizador_id")
	private Organizador organizador;

	@ManyToMany(mappedBy = "eventos")
	private List<Aluno> participantes;

	@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Certificado> certificados;
}