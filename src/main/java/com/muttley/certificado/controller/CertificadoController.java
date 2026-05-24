package com.muttley.certificado.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.muttley.certificado.model.Certificado;
import com.muttley.certificado.services.PdfService;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final PdfService pdfService;

    // O Spring injeta o seu PdfService automaticamente aqui
    public CertificadoController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<byte[]> gerarCertificado(@RequestBody Certificado certificado) {
        try {
            // 1. Chama o serviço que você criou para gerar o array de bytes do PDF
            byte[] pdfBytes = pdfService.gerarCertificado(certificado);

            // 2. Configura os cabeçalhos HTTP para indicar que a resposta é um arquivo de download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            // "attachment" força o navegador a baixar o arquivo. 
            // Você também pode definir o nome padrão do arquivo aqui.
            headers.setContentDispositionFormData("attachment", "certificado_de_participacao.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            // 3. Retorna o PDF com o status 200 OK
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Caso aconteça algum erro na geração do HTML ou PDF, retorna o erro amigável
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}