package com.muttley.inscricao;

import com.muttley.certificado.model.Certificado;
import com.muttley.certificado.repository.CertificadoRepository;
import com.muttley.certificado.services.PdfService;
import com.muttley.email.EmailService;
import com.muttley.evento.Evento;
import com.muttley.pessoa.Pessoa;
import com.muttley.pessoa.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

    public java.util.List<Inscricao> listarPorEvento(Long eventoId) {
        return inscricaoRepository.findByEventoId(eventoId);
    }

    @Transactional
    public void realizarCheckIn(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada."));

        if (inscricao.isPresencaConfirmada()) {
            throw new RuntimeException("O check-in para este participante já foi realizado anteriormente.");
        }

        // Confirma a presença
        inscricao.setPresencaConfirmada(true);
        inscricaoRepository.save(inscricao);

        // Gera o certificado e envia por e-mail, evitando duplicatas
        if (!certificadoRepository.existsByInscricaoId(inscricaoId)) {
            Certificado certificado = gerarCertificado(inscricao);
            dispararEmailComCertificado(certificado, inscricao.getParticipante().getEmail());
        }
    }

    private Certificado gerarCertificado(Inscricao inscricao) {
        // Carrega o participante diretamente para evitar o erro de herança JPA
        // (Organizador extends Pessoa com InheritanceType.JOINED)
        Pessoa participante = pessoaRepository.findByIdComTipoReal(inscricao.getParticipante().getId())
                .orElseThrow(() -> new RuntimeException("Participante não encontrado."));

        Evento evento = inscricao.getEvento();

        Certificado certificado = new Certificado();
        certificado.setInscricaoId(inscricao.getId());
        certificado.setNome("Certificado de Participação");
        certificado.setNomeAluno(participante.getNome());
        certificado.setNomeCurso(evento.getNome());
        certificado.setTema(evento.getDescricao() != null ? evento.getDescricao() : "");
        certificado.setData(evento.getData());
        certificado.setCargaHoraria(
                evento.getCargaHoraria() != null ? evento.getCargaHoraria() + "h" : "");

        return certificadoRepository.save(certificado);
    }

    private void dispararEmailComCertificado(Certificado certificado, String emailDestinatario) {
        try {
            byte[] pdfBytes = pdfService.gerarCertificado(certificado);
            emailService.enviarCertificadoPorEmail(
                    emailDestinatario,
                    certificado.getNomeAluno(),
                    certificado.getTema(),
                    pdfBytes
            );
        } catch (Exception e) {
            // Falha no envio de e-mail não deve reverter o check-in
            System.err.println("Erro ao gerar/enviar certificado por e-mail: " + e.getMessage());
        }
    }
}
