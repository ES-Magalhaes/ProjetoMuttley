package com.muttley.assinante;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assinantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assinante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cargo; // Ex: "Coordenador do curso de ADS", "Diretor da FATEC Zona Leste"

    private String instituicao; // Ex: "FATEC Zona Leste"

    @Column(nullable = false)
    private boolean ativo = true;
}
