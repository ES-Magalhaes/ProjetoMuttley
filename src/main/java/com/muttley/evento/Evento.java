package com.muttley.evento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.muttley.competencia.Competencia;
import com.muttley.inscricao.Inscricao;
import com.muttley.organizador.Organizador;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.FutureOrPresent; // <-- Importação do Validation adicionada
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

	// Alinhado com "nome do evento" dos requisitos
	private String nome;

	@Column(columnDefinition = "TEXT")
	private String descricao;

	// Campos solicitados nos requisitos da professora
	private String categoria; // Ex: Palestra, Workshop, Hackathon, etc.
	private String modalidade; // Ex: Presencial, Online, Híbrido

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@FutureOrPresent(message = "A data do evento não pode ser uma data que já passou.") // <-- Regra de validação
																						// aplicada
	private LocalDate data;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime horaInicio;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime horaFim;

	private String local;

	// Campos numéricos cruciais para a lógica de negócio do sistema
	private Integer cargaHoraria; // Ex: 4 (necessário para o certificado e horas complementares)
	private Integer numeroVagas; // Limite máximo de participantes para controle de inscrições

	// Relacionamento com Organizador/Palestrante
	@ManyToOne
	@JoinColumn(name = "organizador_id")
	private Organizador organizador;

	// Relacionamento correto com a entidade intermediária Inscricao
	@OneToMany(mappedBy = "evento")
	private List<Inscricao> inscricoes;

	// QR Code institucional do evento (Link para a página de inscrição pública)
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String qrCodeBase64;

	// Competências desenvolvidas neste evento (Soft Skills e Hard Skills)
	@ManyToMany
	@JoinTable(name = "evento_competencias", joinColumns = @JoinColumn(name = "evento_id"), inverseJoinColumns = @JoinColumn(name = "competencia_id"))
	private java.util.List<Competencia> competencias = new java.util.ArrayList<>();
}