package com.muttley.organizador;

import com.muttley.evento.EventoRepository;
import com.muttley.medalha.MedalhaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/organizador")
public class OrganizadorController {

	@Autowired
	private OrganizadorService organizadorService;

	@Autowired
	private OrganizadorRepository organizadorRepository;

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private MedalhaService medalhaService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("organizadores", organizadorService.listarTodos());
		return "organizador/listagem";
	}

	@GetMapping("/formulario")
	public String formulario(Model model) {
		model.addAttribute("organizador", new DadosOrganizador(null, "", "", "", "", "", "", "", "", ""));
		return "organizador/formulario";
	}

	@GetMapping("/formulario/{id}")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("organizador", organizadorService.buscarParaEdicao(id));
		return "organizador/formulario";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid @ModelAttribute("organizador") DadosOrganizador dto, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			return "organizador/formulario";
		}
		try {
			organizadorService.salvarOuAtualizar(dto);
			return "redirect:/organizador";
		} catch (Exception e) {
			model.addAttribute("erro", e.getMessage());
			return "organizador/formulario";
		}
	}

	@GetMapping("/delete/{id}")
	public String excluir(@PathVariable Long id) {
		organizadorService.excluir(id);
		return "redirect:/organizador";
	}

	@GetMapping("/perfil/{id}")
	public String perfil(@PathVariable Long id, Model model) {
		Organizador organizador = organizadorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Organizador não encontrado."));
		model.addAttribute("organizador", organizador);
		model.addAttribute("eventos", eventoRepository.findByOrganizadorId(id));
		model.addAttribute("medalhas", medalhaService.listarPorParticipante(id));
		model.addAttribute("totalMedalhas", medalhaService.totalMedalhasPorParticipante(id));
		return "organizador/perfil";
	}
}