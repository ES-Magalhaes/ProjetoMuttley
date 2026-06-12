package com.muttley.medalha;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regras_medalha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegraMedalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome da medalha que será concedida quando a regra for atendida
    @Column(nullable = false)
    private String nomeMedalha;

    @Column(nullable = false, length = 500)
    private String descricaoMedalha;

    @Column(nullable = false)
    private String icone;

    // Tipo de critério que dispara a concessão
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCriterio tipoCriterio;

    // Categoria do evento alvo (usado quando tipoCriterio = PARTICIPACOES_CATEGORIA)
    private String categoriaEvento;

    // Evento específico alvo (usado quando tipoCriterio = EVENTO_ESPECIFICO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private com.muttley.evento.Evento evento;

    // Quantidade mínima para disparar a regra (ex: 3 participações, 20 horas)
    @Column(nullable = false)
    private Integer quantidadeMinima;

    // Ativo/inativo para o admin poder desligar regras sem deletar
    @Column(nullable = false)
    private boolean ativo = true;
}
