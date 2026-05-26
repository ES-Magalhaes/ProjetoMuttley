package com.muttley.inscricao;

import com.muttley.evento.Evento;
import com.muttley.pessoa.Pessoa;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscricoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // O QR Code individual gerado no momento da inscrição
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String qrCodeBase64;

    // Controle de presença para liberação de certificado e medalha
    @Column(nullable = false)
    private boolean presencaConfirmada = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataInscricao = LocalDateTime.now();
}