package com.muttley.organizador;

import com.muttley.evento.Evento;
import com.muttley.pessoa.Pessoa;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "pessoa_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organizador extends Pessoa {

    // Dados exigidos pelo documento para o Palestrante/Organizador
    @Column(columnDefinition = "TEXT")
    private String miniCurriculo; // [cite: 68]

    private String formacao; // [cite: 69]
    private String areaAtuacao; // [cite: 70]
    private String instituicao; // [cite: 71]
    private String linkedin; // [cite: 72]

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String fotoBase64; // [cite: 73] Pode ser URL ou Base64

    // Um organizador/palestrante pode ser responsável por vários eventos
    @OneToMany(mappedBy = "organizador")
    private List<Evento> eventos;
}