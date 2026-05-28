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
        // O fluxo agora lê de forma lógica e sequencial
        Evento evento = buscarEventoOuFalhar(eventoId);
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
        DadosPessoa formLimpo = new DadosPessoa(
                form.id(), form.nome(), form.email(), cpfLimpo,
                form.ra(), form.curso(), form.telefone());
        return pessoaRepository.findByCpf(cpfLimpo)
                .orElseGet(() -> pessoaRepository.save(mapper.toEntity(formLimpo)));
    }

    private void validarInscricaoInedita(Long pessoaId, Long eventoId) {
        if (inscricaoRepository.existsByParticipanteIdAndEventoId(pessoaId, eventoId)) {
            throw new RuntimeException("Participante já está inscrito neste evento.");
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
        return pessoaRepository.findAll().stream()
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
        DadosPessoa dtoLimpo = new DadosPessoa(
                dto.id(), dto.nome(), dto.email(), cpfLimpo,
                dto.ra(), dto.curso(), dto.telefone());

        if (dtoLimpo.id() != null) {
            Pessoa pessoa = pessoaRepository.findById(dtoLimpo.id())
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada."));
            mapper.updateEntityFromDto(dtoLimpo, pessoa);
            pessoaRepository.save(pessoa);
        } else {
            if (pessoaRepository.existsByCpf(cpfLimpo)) {
                throw new RuntimeException("Já existe uma pessoa cadastrada com este CPF.");
            }
            pessoaRepository.save(mapper.toEntity(dtoLimpo));
        }
    }

    @Transactional
    public void excluir(Long id) {
        validarIdSeguro(id);
        if (!pessoaRepository.existsById(id)) {
            throw new RuntimeException("Pessoa não encontrada.");
        }
        pessoaRepository.deleteById(id);
    }

    private void validarIdSeguro(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID fornecido não pode ser nulo.");
        }
    }
}