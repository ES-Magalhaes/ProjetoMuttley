package com.muttley.certificado;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CertificadoMapper {

	@Mapping(source = "aluno.ra", target = "alunoRa")
	@Mapping(source = "evento.id", target = "eventoId")
	@Mapping(source = "nomeCertificado", target = "nome")
	@Mapping(source = "pontuacaoObtida", target = "pontuacao")
	DadosCertificado toDTO(Certificado certificado);

	@Mapping(source = "alunoRa", target = "aluno.ra")
	@Mapping(source = "eventoId", target = "evento.id")
	@Mapping(source = "nome", target = "nomeCertificado")
	@Mapping(source = "pontuacao", target = "pontuacaoObtida")
	Certificado toEntity(DadosCertificado dto);

	@Mapping(source = "alunoRa", target = "aluno.ra")
	@Mapping(source = "eventoId", target = "evento.id")
	@Mapping(source = "nome", target = "nomeCertificado")
	@Mapping(source = "pontuacao", target = "pontuacaoObtida")
	void updateEntityFromDTO(DadosCertificado dto, @MappingTarget Certificado certificado);
}