package com.muttley.evento;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

	@Autowired
	private EventoRepository repository;

	@Autowired
	private EventoMapper mapper;

	public List<Evento> listarTodos() {
		return repository.findAll();
	}

	@Transactional
	public void salvarOuAtualizar(DadosEvento dto) {
		if (dto.id() == null || dto.id() == 0) {
			repository.save(mapper.toEntity(dto));
		} else {
			Evento existente = repository.findById(dto.id())
					.orElseThrow(() -> new RuntimeException("Evento não encontrado"));
			mapper.updateEntityFromDTO(dto, existente);
			repository.save(existente);
		}
	}

	public Evento buscarPorId(Long id) {
		return repository.findById(id).orElseThrow();
	}
}
