package com.muttley.medalha;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.muttley.pessoa.Pessoa;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private String icone;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "data_concessao")
    private LocalDateTime dataConquista;

    @ManyToOne(optional = true)
    @JoinColumn(name = "pessoa_id", nullable = true)
    private Pessoa participante;

    // NOVO CAMPO: Controla se a medalha está ativa/aprovada no sistema
    @Column(nullable = false, length = 20)
    private String status = "PENDENTE"; // Valor padrão inicial
}