package com.muttley.certificado;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CertificadoMapper {

	@Mapping(source = "aluno.ra", target = "alunoRa")
	@Mapping(source = "evento.id", target = "eventoId")
	DadosCertificado toDTO(Certificado certificado);

	@Mapping(source = "alunoRa", target = "aluno.ra")
	@Mapping(source = "eventoId", target = "evento.id")
	Certificado toEntity(DadosCertificado dto);

	@Mapping(source = "alunoRa", target = "aluno.ra")
	@Mapping(source = "eventoId", target = "evento.id")
	void updateEntityFromDTO(DadosCertificado dto, @MappingTarget Certificado certificado);
}