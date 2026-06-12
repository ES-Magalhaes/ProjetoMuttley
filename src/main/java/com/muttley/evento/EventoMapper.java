package com.muttley.evento;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventoMapper {

	@Mapping(source = "organizador.id", target = "organizadorId")
	@Mapping(source = "assinante.id", target = "assinanteId")
	@Mapping(target = "competenciaIds", ignore = true)
	DadosEvento toDTO(Evento evento);

	@Mapping(source = "organizadorId", target = "organizador.id")
	@Mapping(source = "assinanteId", target = "assinante.id")
	@Mapping(target = "qrCodeBase64", ignore = true)
	@Mapping(target = "inscricoes", ignore = true)
	@Mapping(target = "competencias", ignore = true)
	Evento toEntity(DadosEvento dto);

	@Mapping(source = "organizadorId", target = "organizador.id")
	@Mapping(source = "assinanteId", target = "assinante.id")
	@Mapping(target = "qrCodeBase64", ignore = true)
	@Mapping(target = "inscricoes", ignore = true)
	@Mapping(target = "competencias", ignore = true)
	void updateEntityFromDTO(DadosEvento dto, @MappingTarget Evento evento);
}