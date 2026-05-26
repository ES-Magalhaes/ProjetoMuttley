package com.muttley.evento;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventoMapper {

	@Mapping(source = "organizador.id", target = "organizadorId")
	DadosEvento toDTO(Evento evento);

	@Mapping(source = "organizadorId", target = "organizador.id")
	@Mapping(target = "qrCodeBase64", ignore = true)
	@Mapping(target = "inscricoes", ignore = true)
	Evento toEntity(DadosEvento dto);

	@Mapping(source = "organizadorId", target = "organizador.id")
	@Mapping(target = "qrCodeBase64", ignore = true)
	@Mapping(target = "inscricoes", ignore = true)
	void updateEntityFromDTO(DadosEvento dto, @MappingTarget Evento evento);
}