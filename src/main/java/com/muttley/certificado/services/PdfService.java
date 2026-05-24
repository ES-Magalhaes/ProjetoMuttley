package com.muttley.certificado.services;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.muttley.certificado.model.Certificado;

@Service
public class PdfService {
    private final SpringTemplateEngine templateEngine;

    // O Spring injeta isso automaticamente
    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] gerarCertificado(Certificado certificado) throws Exception {
        
        // Agora está usando o Context do Thymeleaf
        Context context = new Context();
        
        // Em vez de passar string por string, você pode passar o objeto inteiro para o HTML!
        context.setVariable("certificado", certificado); 

        String html = templateEngine.process("certificado/certificado", context);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}