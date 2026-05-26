package com.muttley.organizador;

import org.springframework.stereotype.Component;

@Component
public class OrganizadorMapper {

	public DadosOrganizador toDto(Organizador entity) {
		return new DadosOrganizador(
				entity.getId(),
				entity.getNome(),
				entity.getEmail(),
				entity.getCpf(),
				entity.getMiniCurriculo(),
				entity.getFormacao(),
				entity.getAreaAtuacao(),
				entity.getInstituicao(),
				entity.getLinkedin(),
				entity.getFotoBase64());
	}

	public Organizador toEntity(DadosOrganizador dto) {
		Organizador entity = new Organizador();
		entity.setId(dto.id());
		updateEntityFromDto(dto, entity);
		return entity;
	}

	public void updateEntityFromDto(DadosOrganizador dto, Organizador entity) {
		entity.setNome(dto.nome());
		entity.setEmail(dto.email());
		entity.setCpf(dto.cpf());
		entity.setMiniCurriculo(dto.miniCurriculo());
		entity.setFormacao(dto.formacao());
		entity.setAreaAtuacao(dto.areaAtuacao());
		entity.setInstituicao(dto.instituicao());
		entity.setLinkedin(dto.linkedin());
		entity.setFotoBase64(dto.fotoBase64());
	}
}