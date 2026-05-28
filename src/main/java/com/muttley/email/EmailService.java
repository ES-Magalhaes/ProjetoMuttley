package com.muttley.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    /**
     * Envia um e-mail com o certificado em PDF anexado.
     * Marcado como @Async para não bloquear a thread principal durante o check-in.
     */
    @Async
    public void enviarCertificadoPorEmail(String para, String nomeAluno,
                                          String nomeEvento, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(para);
            helper.setSubject("Seu Certificado - " + nomeEvento);

            String corpo = "<html><body>"
                    + "<h2>Olá, " + nomeAluno + "!</h2>"
                    + "<p>Parabéns pela sua participação no evento <strong>" + nomeEvento + "</strong>.</p>"
                    + "<p>Segue em anexo o seu certificado de participação.</p>"
                    + "<br><p>Atenciosamente,<br><strong>Equipe Projeto Muttley - FATEC Zona Leste </strong></p>"
                    + "</body></html>";

            helper.setText(corpo, true);

            String nomeArquivo = "certificado_" + nomeAluno.replaceAll("\\s+", "_") + ".pdf";
            helper.addAttachment(nomeArquivo, new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (MessagingException e) {
            // Loga o erro sem interromper o fluxo — o certificado já foi salvo no banco
            System.err.println("Falha ao enviar e-mail para " + para + ": " + e.getMessage());
        }
    }
}
