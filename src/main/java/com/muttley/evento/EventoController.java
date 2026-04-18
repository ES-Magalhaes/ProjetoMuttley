package com.muttley.evento;

import com.muttley.organizador.OrganizadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
		model.addAttribute("evento", new DadosEvento(null, "", "", "", null, null));
		model.addAttribute("organizadores", organizadorService.listarTodos());
		return "evento/formulario";
	}

	@GetMapping("/formulario/{id}")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("evento", eventoService.buscarParaEdicao(id));
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

	@GetMapping("/delete/{id}")
	public String excluir(@PathVariable Long id) {
		eventoService.excluir(id);
		return "redirect:/evento";
	}
}