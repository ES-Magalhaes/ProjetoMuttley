package com.muttley.competencia;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; // Ex: "Comunicação", "Excel", "Liderança"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCompetencia tipo; // SOFT_SKILL ou HARD_SKILL

    public enum TipoCompetencia {
        SOFT_SKILL, HARD_SKILL
    }
}
