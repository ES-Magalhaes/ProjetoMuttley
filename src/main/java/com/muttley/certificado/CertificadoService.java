package com.muttley.certificado;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CertificadoService {

	@Autowired
	private CertificadoRepository repository;

	@Autowired
	private CertificadoMapper mapper;

	// Lista todos os certificados para a página de listagem
	public List<Certificado> listarTodos() {
		return repository.findAll();
	}

	// Salva um novo certificado ou atualiza um existente
	@Transactional
	public void salvarOuAtualizar(DadosCertificado dto) {
		if (dto.id() == null || dto.id() == 0) {
			// Criação de novo certificado
			Certificado novoCertificado = mapper.toEntity(dto);
			repository.save(novoCertificado);
		} else {
			// Atualização de certificado existente
			Certificado existente = repository.findById(dto.id())
					.orElseThrow(() -> new EntityNotFoundException("Certificado não encontrado com ID: " + dto.id()));

			mapper.updateEntityFromDTO(dto, existente);
			repository.save(existente);
		}
	}

	// Busca um certificado específico pelo ID (útil para edição)
	public Certificado buscarPorId(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Certificado não encontrado com ID: " + id));
	}

	// Remove um certificado
	@Transactional
	public void excluir(Long id) {
		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("Não é possível excluir: Certificado não encontrado.");
		}
		repository.deleteById(id);
	}
}
