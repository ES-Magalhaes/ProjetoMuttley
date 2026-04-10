package com.muttley.aluno;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AlunoService {

	@Autowired
	private AlunoRepository ar;

	@Autowired
	private AlunoMapper mapper;

	public Aluno salvar(Aluno aluno) {
		return ar.save(aluno);
	}

	public List<Aluno> procurarTodos() {
		return ar.findAll(Sort.by("nome").ascending());
	}

	public void apagarPorRA(Long ra) {
		ar.deleteById(ra);
	}

	public Optional<Aluno> procurarPorRA(Long ra) {
		return ar.findById(ra);
	}

	@Transactional
	public void atualizarAluno(AtualizacaoAluno dados) {
		// Busca o aluno existente
		Aluno aluno = ar.findById(dados.ra())
				.orElseThrow(() -> new EntityNotFoundException("Aluno com RA " + dados.ra() + " não encontrado"));

		// MapStruct mapeia os dados do Record para a Entidade automaticamente
		mapper.updateEntityFromDTO(dados, aluno);
		ar.save(aluno);
	}

	@Transactional
	public Aluno salvarOuAtualizar(AtualizacaoAluno dto) {
		if (dto.ra() == null) { // Se for null, o MapStruct cria uma nova entidade
			Aluno novoAluno = mapper.toEntity(dto);
			return ar.save(novoAluno);
		} else {
			// Se tiver RA, busca e atualiza
			Aluno alunoExistente = ar.findById(dto.ra())
					.orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
			mapper.updateEntityFromDTO(dto, alunoExistente);
			return ar.save(alunoExistente);
		}
	}
}