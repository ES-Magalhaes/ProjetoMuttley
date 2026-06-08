package com.muttley.pessoa;

import org.springframework.stereotype.Component;

@Component
public class PessoaMapper {

    public DadosPessoa toDto(Pessoa entity) {
        return new DadosPessoa(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getRa(),
                entity.getCurso());
    }

    public Pessoa toEntity(DadosPessoa dto) {
        Pessoa entity = new Pessoa();
        entity.setId(dto.id());
        updateEntityFromDto(dto, entity);
        return entity;
    }

    public void updateEntityFromDto(DadosPessoa dto, Pessoa entity) {
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setCpf(dto.cpf());
        entity.setRa(dto.ra());
        entity.setCurso(dto.curso());
    }
}