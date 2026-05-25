package com.muttley.organizador;

import com.muttley.evento.Evento;
import com.muttley.pessoa.Pessoa; // Importação da classe pai
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "pessoa_id") // O ID da tabela Organizador apontará para o ID de Pessoa
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organizador extends Pessoa {

    // nome e id foram removidos pois são herdados de Pessoa
    private String qualificacoes;

    @OneToMany(mappedBy = "organizador")
    private List<Evento> eventos;
}