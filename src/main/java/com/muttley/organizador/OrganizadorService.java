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
		// Validação de CPF
		String cpfLimpo = dto.cpf().replaceAll("\\D", "");
		if (!isValidCpf(cpfLimpo)) {
			throw new RuntimeException("CPF inválido. Verifique os dígitos informados.");
		}

		Organizador organizador;

		if (dto.id() != null) {
			organizador = repository.findById(dto.id())
					.orElseThrow(() -> new RuntimeException("Organizador não encontrado"));
			mapper.updateEntityFromDto(dto, organizador);
			organizador.setCpf(cpfLimpo);
		} else {
			if (repository.existsByCpf(cpfLimpo)) {
				throw new RuntimeException("Já existe um organizador cadastrado com este CPF.");
			}
			organizador = mapper.toEntity(dto);
			organizador.setCpf(cpfLimpo);
		}

		repository.save(organizador);
	}

	private boolean isValidCpf(String cpf) {
		if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
		try {
			int soma = 0;
			for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
			int dig1 = 11 - (soma % 11);
			if (dig1 > 9) dig1 = 0;
			if (dig1 != (cpf.charAt(9) - '0')) return false;

			soma = 0;
			for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
			int dig2 = 11 - (soma % 11);
			if (dig2 > 9) dig2 = 0;
			return dig2 == (cpf.charAt(10) - '0');
		} catch (Exception e) {
			return false;
		}
	}

	@Autowired
	private com.muttley.evento.EventoRepository eventoRepository;

	@Autowired
	private com.muttley.medalha.MedalhaRepository medalhaRepository;

	@Transactional
	public void excluir(Long id) {
		if (!repository.existsById(id)) {
			throw new RuntimeException("Organizador não encontrado.");
		}
		// Verifica se tem eventos vinculados
		java.util.List<com.muttley.evento.Evento> eventos = eventoRepository.findByOrganizadorId(id);
		if (!eventos.isEmpty()) {
			String nomesEventos = eventos.stream()
					.map(com.muttley.evento.Evento::getNome)
					.collect(java.util.stream.Collectors.joining(", "));
			throw new RuntimeException("Não é possível excluir este organizador. "
					+ "Ele está vinculado aos seguintes eventos: " + nomesEventos
					+ ". Remova o vínculo nos eventos antes de excluir.");
		}
		// Remove medalhas vinculadas ao organizador
		java.util.List<com.muttley.medalha.Medalha> medalhas = medalhaRepository.findByParticipanteId(id);
		if (!medalhas.isEmpty()) {
			medalhaRepository.deleteAll(medalhas);
		}
		repository.deleteById(id);
	}
}