package com.muttley.pessoa;

import com.muttley.evento.Evento;
import com.muttley.evento.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Transactional
    public void inscreverPessoaEmEvento(DadosPessoa form, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(form.nome());
        pessoa.setEmail(form.email());
        pessoa.setCpf(form.cpf());

        if (pessoa.getEventosInscritos() == null) {
            pessoa.setEventosInscritos(new ArrayList<>());
        }

        // Relacionamento bidirecional
        pessoa.getEventosInscritos().add(evento);

        pessoaRepository.save(pessoa);
    }
}