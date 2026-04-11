package com.muttley.organizador;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizadorMapper {

	DadosOrganizador toDTO(Organizador organizador);

	Organizador toEntity(DadosOrganizador dto);

	void updateEntityFromDTO(DadosOrganizador dto, @MappingTarget Organizador organizador);
}
