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

	// Este método busca a entidade e já a transforma no DTO que o formulário espera
	public DadosCertificado buscarParaEdicao(Long id) {
		Certificado certificado = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Certificado não encontrado"));

		return mapper.toDTO(certificado);
	}

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
		// Verifica se existe antes de tentar deletar para evitar erros chatos
		if (repository.existsById(id)) {
			repository.deleteById(id);
		}
	}
}
