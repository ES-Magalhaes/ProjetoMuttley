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
        // Usa o Mapper para instanciar a pessoa inteira em uma linha
        return pessoaRepository.findByCpf(form.cpf())
                .orElseGet(() -> pessoaRepository.save(mapper.toEntity(form)));
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

    public DadosPessoa buscarParaEdicao(Long id) {
        validarIdSeguro(id);
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada."));
        return mapper.toDto(pessoa);
    }

    @Transactional
    public void salvarOuAtualizar(DadosPessoa dto) {
        if (dto.id() != null) {
            Pessoa pessoa = pessoaRepository.findById(dto.id())
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada."));
            mapper.updateEntityFromDto(dto, pessoa);
            pessoaRepository.save(pessoa);
        } else {
            if (pessoaRepository.existsByCpf(dto.cpf())) {
                throw new RuntimeException("Já existe uma pessoa cadastrada com este CPF.");
            }
            pessoaRepository.save(mapper.toEntity(dto));
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