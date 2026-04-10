package com.muttley.aluno;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AtualizacaoAluno(
    Long ra,
    
    @NotBlank(message = "Nome é obrigatório") 
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    String email,
    
    @NotNull(message = "Pontos é obrigatório")
    @PositiveOrZero(message = "Pontuação não pode ser negativa")
    Integer pontos
){}