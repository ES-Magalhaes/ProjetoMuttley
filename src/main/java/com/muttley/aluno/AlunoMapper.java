package com.muttley.aluno;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlunoMapper {

    // Converte de Entidade para DTO (útil para retornar dados ao frontend)
    AtualizacaoAluno toDTO(Aluno aluno);
    
    // Converte de DTO para Entidade (útil para novos cadastros)
    @Mapping(target = "ra", ignore = true)
    Aluno toEntity(AtualizacaoAluno dto);
    
    // Atualiza uma instância existente (substitui o seu antigo atualizarInfos)
    @Mapping(target = "ra", ignore = true)
    void updateEntityFromDTO(AtualizacaoAluno dto, @MappingTarget Aluno aluno);
}