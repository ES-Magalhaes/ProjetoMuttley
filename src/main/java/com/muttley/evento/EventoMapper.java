package com.muttley.evento;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventoMapper {

	@Mapping(source = "organizador.id", target = "organizadorId")
	DadosEvento toDTO(Evento evento);

	@Mapping(source = "organizadorId", target = "organizador.id")
	Evento toEntity(DadosEvento dto);

	@Mapping(source = "organizadorId", target = "organizador.id")
	void updateEntityFromDTO(DadosEvento dto, @MappingTarget Evento evento);
}
