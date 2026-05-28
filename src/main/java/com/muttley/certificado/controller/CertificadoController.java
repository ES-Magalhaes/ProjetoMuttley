package com.muttley.certificado.controller;

import com.muttley.certificado.model.Certificado;
import com.muttley.certificado.services.CertificadoService;
import com.muttley.certificado.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/certificado")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @Autowired
    private PdfService pdfService;

    // =========================================================================
    // ROTAS DE TELA (Thymeleaf)
    // =========================================================================

    // GET /certificado — listagem de todos os certificados
    @GetMapping
    public String listarTodos(Model model) {
        model.addAttribute("certificados", certificadoService.listarTodos());
        return "certificado/listagem";
    }

    // GET /certificado/formulario — formulário para novo certificado
    @GetMapping("/formulario")
    public String exibirFormularioNovo(Model model) {
        model.addAttribute("certificado", new Certificado());
        return "certificado/formulario";
    }

    // GET /certificado/formulario/{id} — formulário preenchido para edição
    @GetMapping("/formulario/{id}")
    public String exibirFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("certificado", certificadoService.buscarPorId(id));
        return "certificado/formulario";
    }

    // POST /certificado/salvar — salva ou atualiza o certificado
    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Certificado certificado,
            Model model) {
        try {
            certificadoService.salvar(certificado);
            return "redirect:/certificado";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "certificado/formulario";
        }
    }

    // GET /certificado/delete/{id} — exclui o certificado
    @GetMapping("/delete/{id}")
    public String excluir(@PathVariable Long id) {
        certificadoService.excluir(id);
        return "redirect:/certificado";
    }

    // GET /certificado/gerar/{id} — gera e baixa o PDF do certificado
    @GetMapping("/gerar/{id}")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            Certificado certificado = certificadoService.buscarPorId(id);
            byte[] pdfBytes = pdfService.gerarCertificado(certificado);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "certificado_" + certificado.getNomeAluno() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================================================
    // ROTA DE API (mantida para compatibilidade)
    // =========================================================================

    @PostMapping("/api/gerar")
    @ResponseBody
    public ResponseEntity<byte[]> gerarCertificadoApi(@RequestBody Certificado certificado) {
        try {
            byte[] pdfBytes = pdfService.gerarCertificado(certificado);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "certificado_de_participacao.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
