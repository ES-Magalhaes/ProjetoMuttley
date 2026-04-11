package com.muttley.organizador;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
public class OrganizadorService {

	@Autowired
	private OrganizadorRepository repository;

	@Autowired
	private OrganizadorMapper mapper;

	public List<Organizador> listarTodos() {
		return repository.findAll();
	}

	@Transactional
	public Organizador salvarOuAtualizar(DadosOrganizador dto) {
		if (dto.id() == null || dto.id() == 0) {
			return repository.save(mapper.toEntity(dto));
		} else {
			Organizador existente = repository.findById(dto.id())
					.orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));
			mapper.updateEntityFromDTO(dto, existente);
			return repository.save(existente);
		}
	}

	public Organizador buscarPorId(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));
	}

	@Transactional
	public void excluir(Long id) {
		repository.deleteById(id);
	}
}