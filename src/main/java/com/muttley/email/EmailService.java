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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    /**
     * Envia e-mail com certificado PDF anexado + botão para adicionar ao LinkedIn.
     */
    @Async
    public void enviarCertificadoPorEmail(String para, String nomeAluno,
                                          String nomeEvento, byte[] pdfBytes,
                                          Long certificadoId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(para);
            helper.setSubject("Seu Certificado - " + nomeEvento);

            // Monta URL do LinkedIn para adicionar certificação
            String linkedinUrl = buildLinkedinUrl(nomeEvento, certificadoId);

            String corpo = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color: #2c3e50;'>Olá, " + nomeAluno + "!</h2>"
                    + "<p>Parabéns pela sua participação no evento <strong>" + nomeEvento + "</strong>.</p>"
                    + "<p>Segue em anexo o seu certificado de participação.</p>"
                    + "<br>"
                    + "<p style='margin-bottom: 8px;'><strong>Adicione ao seu perfil profissional:</strong></p>"
                    + "<a href='" + linkedinUrl + "' target='_blank' "
                    + "style='display: inline-block; background: #0077b5; color: #ffffff; "
                    + "padding: 12px 24px; border-radius: 6px; text-decoration: none; "
                    + "font-weight: 600; font-size: 14px;'>"
                    + "&#x1F517; Adicionar ao LinkedIn</a>"
                    + "<br><br>"
                    + "<p style='color: #888; font-size: 12px;'>Ao clicar, o LinkedIn abrirá com os dados "
                    + "do certificado preenchidos. Basta confirmar para salvar no seu perfil.</p>"
                    + "<br>"
                    + "<p>Atenciosamente,<br><strong>Equipe Projeto Muttley - FATEC Zona Leste</strong></p>"
                    + "</body></html>";

            helper.setText(corpo, true);

            String nomeArquivo = "certificado_" + nomeAluno.replaceAll("\\s+", "_") + ".pdf";
            helper.addAttachment(nomeArquivo, new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Falha ao enviar e-mail para " + para + ": " + e.getMessage());
        }
    }

    private String buildLinkedinUrl(String nomeEvento, Long certificadoId) {
        LocalDate hoje = LocalDate.now();
        String certName = "Certificado de Participação - " + nomeEvento;
        String certUrl = "http://localhost:8081/certificado/gerar/" + certificadoId;

        return "https://www.linkedin.com/profile/add?startTask=CERTIFICATION_NAME"
                + "&name=" + encode(certName)
                + "&organizationName=" + encode("FATEC Zona Leste")
                + "&issueYear=" + hoje.getYear()
                + "&issueMonth=" + hoje.getMonthValue()
                + "&certUrl=" + encode(certUrl)
                + "&certId=" + certificadoId;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
