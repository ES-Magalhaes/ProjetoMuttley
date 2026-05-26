package com.muttley.evento;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.muttley.inscricao.QrCodeService;

@Service
public class EventoService {

	@Autowired
	private EventoRepository repository;

	@Autowired
	private EventoMapper mapper;

	@Autowired
	private QrCodeService qrCodeService;

	public List<Evento> listarTodos() {
		return repository.findAll();
	}

	@Transactional
	public Evento salvarOuAtualizar(DadosEvento dto) {
		if (dto.horaFim().isBefore(dto.horaInicio())) {
			throw new IllegalArgumentException("A hora de término não pode ser anterior à hora de início.");
		}

		if (dto.id() == null || dto.id() == 0) {
			Evento novoEvento = mapper.toEntity(dto);
			novoEvento = repository.save(novoEvento); // Salva para gerar o ID

			String urlInscricao = "http://localhost:8081/evento/inscrever/" + novoEvento.getId();
			String qrCode = qrCodeService.gerarQrCodeBase64(urlInscricao, 250, 250);
			novoEvento.setQrCodeBase64(qrCode);

			return repository.save(novoEvento); // Retorna a entidade salva com o QR Code
		} else {
			Evento existente = repository.findById(dto.id())
					.orElseThrow(() -> new RuntimeException("Evento não encontrado"));
			mapper.updateEntityFromDTO(dto, existente);
			return repository.save(existente); // Retorna a entidade atualizada
		}
	}

	public Evento buscarPorId(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
	}

	public DadosEvento buscarParaEdicao(Long id) {
		Evento evento = buscarPorId(id);
		return mapper.toDTO(evento);
	}

	@Transactional
	public void excluir(Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		}
	}
}