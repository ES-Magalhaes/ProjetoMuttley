package com.muttley.evento;

import com.muttley.competencia.Competencia;
import com.muttley.competencia.CompetenciaRepository;
import java.util.ArrayList;
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

	@Autowired
	private CompetenciaRepository competenciaRepository;

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
			aplicarCompetencias(novoEvento, dto.competenciaIds());
			novoEvento = repository.save(novoEvento);

			String urlInscricao = "http://localhost:8081/evento/inscrever/" + novoEvento.getId();
			String qrCode = qrCodeService.gerarQrCodeBase64(urlInscricao, 250, 250);
			novoEvento.setQrCodeBase64(qrCode);

			return repository.save(novoEvento);
		} else {
			Evento existente = repository.findById(dto.id())
					.orElseThrow(() -> new RuntimeException("Evento não encontrado"));
			mapper.updateEntityFromDTO(dto, existente);
			aplicarCompetencias(existente, dto.competenciaIds());
			return repository.save(existente);
		}
	}

	private void aplicarCompetencias(Evento evento, List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			evento.setCompetencias(new ArrayList<>());
			return;
		}
		List<Competencia> competencias = competenciaRepository.findAllById(ids);
		evento.setCompetencias(competencias);
	}

	public Evento buscarPorId(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
	}

	public DadosEvento buscarParaEdicao(Long id) {
		Evento evento = buscarPorId(id);
		return mapper.toDTO(evento);
	}

	@Autowired
	private com.muttley.inscricao.InscricaoRepository inscricaoRepository;

	@Transactional
	public void excluir(Long id) {
		if (!repository.existsById(id)) {
			throw new RuntimeException("Evento não encontrado.");
		}

		// Verifica se algum participante já fez check-in
		List<com.muttley.inscricao.Inscricao> inscricoes = inscricaoRepository.findByEventoId(id);
		boolean temCheckin = inscricoes.stream().anyMatch(com.muttley.inscricao.Inscricao::isPresencaConfirmada);

		if (temCheckin) {
			throw new RuntimeException("Não é possível excluir este evento. "
					+ "Já existem participantes com check-in realizado.");
		}

		// Remove inscrições pendentes (sem check-in) antes de excluir o evento
		if (!inscricoes.isEmpty()) {
			inscricaoRepository.deleteAll(inscricoes);
		}

		repository.deleteById(id);
	}
}
