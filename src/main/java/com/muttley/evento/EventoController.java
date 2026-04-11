package com.muttley.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.muttley.organizador.OrganizadorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private EventoService eventoService;

	@Autowired
	private OrganizadorService organizadorService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("eventos", eventoService.listarTodos());
		return "evento/listagem";
	}

	@GetMapping("/formulario")
	public String formulario(Model model) {
		model.addAttribute("evento", new DadosEvento(null, "", "", 0, null));
		// IMPORTANTE: Enviamos a lista de organizadores para o Select
		model.addAttribute("organizadores", organizadorService.listarTodos());
		return "evento/formulario";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid @ModelAttribute("evento") DadosEvento dto, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("organizadores", organizadorService.listarTodos());
			return "evento/formulario";
		}
		eventoService.salvarOuAtualizar(dto);
		return "redirect:/evento";
	}
}
