package com.muttley.organizador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrganizadorService {

	@Autowired
	private OrganizadorRepository repository;

	@Autowired
	private OrganizadorMapper mapper;

	public List<DadosOrganizador> listarTodos() {
		return repository.findAll().stream()
				.map(mapper::toDto)
				.toList();
	}

	public DadosOrganizador buscarParaEdicao(Long id) {
		Organizador organizador = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Organizador não encontrado"));
		return mapper.toDto(organizador);
	}

	@Transactional
	public void salvarOuAtualizar(DadosOrganizador dto) {
		Organizador organizador;

		if (dto.id() != null) {
			organizador = repository.findById(dto.id())
					.orElseThrow(() -> new RuntimeException("Organizador não encontrado"));
			mapper.updateEntityFromDto(dto, organizador);
		} else {
			if (repository.existsByCpf(dto.cpf())) {
				throw new RuntimeException("Já existe um organizador cadastrado com este CPF.");
			}
			organizador = mapper.toEntity(dto);
		}

		repository.save(organizador);
	}

	@Transactional
	public void excluir(Long id) {
		if (!repository.existsById(id)) {
			throw new RuntimeException("Organizador não encontrado");
		}
		repository.deleteById(id);
	}
}