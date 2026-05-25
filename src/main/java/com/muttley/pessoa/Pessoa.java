package com.muttley.pessoa;

import com.muttley.evento.Evento;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Cria tabelas separadas unidas pelo ID
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cpf;
    private String nome;
    private String email;

    // Novo relacionamento: Lista de eventos que esta pessoa se inscreveu
    @ManyToMany
    @JoinTable(name = "pessoa_evento", joinColumns = @JoinColumn(name = "pessoa_id"), inverseJoinColumns = @JoinColumn(name = "evento_id"))
    private List<Evento> eventosInscritos;
}
