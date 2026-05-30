package com.muttley.pessoa;

import com.muttley.evento.Evento;
import com.muttley.evento.EventoRepository;
import com.muttley.inscricao.Inscricao;
import com.muttley.inscricao.InscricaoRepository;
import com.muttley.inscricao.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // O Lombok cria os construtores e injeta tudo sem precisar de @Autowired
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final EventoRepository eventoRepository;
    private final InscricaoRepository inscricaoRepository;
    private final QrCodeService qrCodeService;
    private final PessoaMapper mapper;

    @Transactional
    public String inscreverPessoaEmEvento(DadosPessoa form, Long eventoId) {
        Evento evento = buscarEventoOuFalhar(eventoId);

        // Validação de vagas
        validarVagasDisponiveis(evento);

        Pessoa pessoa = buscarOuCriarPessoa(form);

        validarInscricaoInedita(pessoa.getId(), eventoId);

        Inscricao inscricao = criarESalvarInscricao(pessoa, evento);

        return gerarEAtribuirQrCode(inscricao);
    }

    private Evento buscarEventoOuFalhar(Long eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    private Pessoa buscarOuCriarPessoa(DadosPessoa form) {
        // Remove formatação do CPF antes de qualquer operação
        String cpfLimpo = form.cpf().replaceAll("\\D", "");

        // Validação de CPF
        if (!isValidCpf(cpfLimpo)) {
            throw new RuntimeException("CPF inválido. Verifique os dígitos informados.");
        }

        // Busca o ID via query nativa para evitar carregar proxy com tipo errado
        java.util.Optional<Long> pessoaId = pessoaRepository.findIdByCpf(cpfLimpo);
        if (pessoaId.isPresent()) {
            return pessoaRepository.findById(pessoaId.get())
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada."));
        }

        // Validação de RA único ao criar nova pessoa na inscrição
        if (form.ra() != null && !form.ra().isBlank()) {
            java.util.Optional<Long> raExistente = pessoaRepository.findIdByRa(form.ra());
            if (raExistente.isPresent()) {
                throw new RuntimeException("Já existe um participante cadastrado com o RA: " + form.ra());
            }
        }

        // Cria nova pessoa
        DadosPessoa formLimpo = new DadosPessoa(
                null, form.nome(), form.email(), cpfLimpo,
                form.ra(), form.curso(), form.telefone());
        return pessoaRepository.save(mapper.toEntity(formLimpo));
    }

    private void validarInscricaoInedita(Long pessoaId, Long eventoId) {
        if (inscricaoRepository.existsByParticipanteIdAndEventoId(pessoaId, eventoId)) {
            throw new RuntimeException("Participante já está inscrito neste evento.");
        }
    }

    private void validarVagasDisponiveis(Evento evento) {
        if (evento.getNumeroVagas() != null) {
            long inscritosAtual = inscricaoRepository.countByEventoId(evento.getId());
            if (inscritosAtual >= evento.getNumeroVagas()) {
                throw new RuntimeException("Não há mais vagas disponíveis para este evento. (" 
                    + inscritosAtual + "/" + evento.getNumeroVagas() + ")");
            }
        }
    }

    private Inscricao criarESalvarInscricao(Pessoa pessoa, Evento evento) {
        Inscricao inscricao = new Inscricao();
        inscricao.setParticipante(pessoa);
        inscricao.setEvento(evento);
        inscricao.setPresencaConfirmada(false);
        return inscricaoRepository.save(inscricao);
    }

    private String gerarEAtribuirQrCode(Inscricao inscricao) {
        String urlCheckIn = "http://localhost:8081/evento/checkin/" + inscricao.getId();
        String qrCodeBase64 = qrCodeService.gerarQrCodeBase64(urlCheckIn, 250, 250);

        inscricao.setQrCodeBase64(qrCodeBase64);
        inscricaoRepository.save(inscricao);

        return qrCodeBase64;
    }

    public List<DadosPessoa> listarTodos() {
        // Retorna apenas pessoas que têm pelo menos uma inscrição (exclui organizadores sem inscrição)
        return pessoaRepository.findAll().stream()
                .filter(p -> inscricaoRepository.existsByParticipanteId(p.getId()))
                .map(mapper::toDto)
                .toList();
    }

    public java.util.Optional<DadosPessoa> buscarPorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return pessoaRepository.findByCpf(cpfLimpo).map(mapper::toDto);
    }

    public DadosPessoa buscarParaEdicao(Long id) {
        validarIdSeguro(id);
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada."));
        return mapper.toDto(pessoa);
    }

    @Transactional
    public void salvarOuAtualizar(DadosPessoa dto) {
        // Garante que o CPF é sempre salvo sem formatação
        String cpfLimpo = dto.cpf().replaceAll("\\D", "");

        // Validação de CPF (11 dígitos)
        if (!isValidCpf(cpfLimpo)) {
            throw new RuntimeException("CPF inválido. Verifique os dígitos informados.");
        }

        DadosPessoa dtoLimpo = new DadosPessoa(
                dto.id(), dto.nome(), dto.email(), cpfLimpo,
                dto.ra(), dto.curso(), dto.telefone());

        if (dtoLimpo.id() != null) {
            Pessoa pessoa = pessoaRepository.findById(dtoLimpo.id())
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada."));
            // Validação de RA único na edição
            validarRaUnico(dtoLimpo.ra(), dtoLimpo.id());
            mapper.updateEntityFromDto(dtoLimpo, pessoa);
            pessoaRepository.save(pessoa);
        } else {
            if (pessoaRepository.existsByCpf(cpfLimpo)) {
                throw new RuntimeException("Já existe uma pessoa cadastrada com este CPF.");
            }
            // Validação de RA único no cadastro
            validarRaUnico(dtoLimpo.ra(), null);
            pessoaRepository.save(mapper.toEntity(dtoLimpo));
        }
    }

    private void validarRaUnico(String ra, Long idAtual) {
        if (ra == null || ra.isBlank()) return;
        java.util.Optional<Long> existente = pessoaRepository.findIdByRa(ra);
        if (existente.isPresent() && !existente.get().equals(idAtual)) {
            throw new RuntimeException("Já existe um participante cadastrado com o RA: " + ra);
        }
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

    @Transactional
    public void excluir(Long id) {
        validarIdSeguro(id);
        if (!pessoaRepository.existsById(id)) {
            throw new RuntimeException("Pessoa não encontrada.");
        }
        // Remove todas as inscrições vinculadas para evitar constraint SQL
        java.util.List<com.muttley.inscricao.Inscricao> inscricoes = inscricaoRepository.findByParticipanteId(id);
        if (!inscricoes.isEmpty()) {
            inscricaoRepository.deleteAll(inscricoes);
        }
        pessoaRepository.deleteById(id);
    }

    private void validarIdSeguro(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID fornecido não pode ser nulo.");
        }
    }
}