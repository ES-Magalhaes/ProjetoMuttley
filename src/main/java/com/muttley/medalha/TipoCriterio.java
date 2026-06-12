package com.muttley.medalha;

public enum TipoCriterio {
    PARTICIPACOES_CATEGORIA,  // X participações em eventos de uma categoria específica
    PARTICIPACOES_TOTAL,      // X participações em qualquer evento
    CARGA_HORARIA,            // X horas acumuladas
    ORGANIZADOR,              // Ser organizador de pelo menos X eventos
    EVENTO_ESPECIFICO         // Participou de um evento específico (pelo ID)
}
