package com.muttley.medalha;

import com.muttley.evento.Evento;
import com.muttley.pessoa.Pessoa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medalhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMedalha tipo;

    @Column(nullable = false)
    private LocalDate dataConcessao;

    // Referência à inscrição que originou esta medalha (evita duplicatas)
    @Column(unique = true)
    private Long inscricaoId;

    // Controle de validação pelo administrador
    @Column(nullable = false)
    private boolean validada = false;

    private String observacaoValidacao;

    // Nome da competência (usado apenas para medalhas do tipo COMPETENCIA)
    private String competenciaNome;

    public enum TipoMedalha {
        PARTICIPACAO("Medalha de Participação"),
        CONCLUSAO("Medalha de Conclusão"),
        ORGANIZACAO("Medalha de Organização"),
        APRESENTACAO("Medalha de Apresentação"),
        COMPETENCIA("Medalha de Competência");

        private final String descricao;

        TipoMedalha(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
