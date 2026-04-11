package com.muttley.certificado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.muttley.aluno.AlunoService;
import com.muttley.evento.EventoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/certificado")
public class CertificadoController {

	@Autowired
	private CertificadoService certificadoService;
	@Autowired
	private AlunoService alunoService;
	@Autowired
	private EventoService eventoService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("certificados", certificadoService.listarTodos());
		return "certificado/listagem";
	}

	@GetMapping("/formulario")
	public String formulario(Model model) {
		model.addAttribute("certificado", new DadosCertificado(null, "", 0, "", null, null));
		model.addAttribute("alunos", alunoService.procurarTodos());
		model.addAttribute("eventos", eventoService.listarTodos());
		return "certificado/formulario";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid @ModelAttribute("certificado") DadosCertificado dto, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("alunos", alunoService.procurarTodos());
			model.addAttribute("eventos", eventoService.listarTodos());
			return "certificado/formulario";
		}
		certificadoService.salvarOuAtualizar(dto);
		return "redirect:/certificado";
	}
}
