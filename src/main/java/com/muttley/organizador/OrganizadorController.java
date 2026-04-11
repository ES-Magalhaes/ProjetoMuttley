package com.muttley.organizador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/organizador")
public class OrganizadorController {

	@Autowired
	private OrganizadorService organizadorService;

	@Autowired
	private OrganizadorMapper organizadorMapper;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("organizadores", organizadorService.listarTodos());
		return "organizador/listagem";
	}

	@GetMapping("/formulario")
	public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
		DadosOrganizador dto;
		if (id != null) {
			Organizador org = organizadorService.buscarPorId(id);
			dto = organizadorMapper.toDTO(org);
		} else {
			dto = new DadosOrganizador(null, "", "", "");
		}
		model.addAttribute("organizador", dto);
		return "organizador/formulario";
	}

	@PostMapping("/salvar")
	public String salvar(@ModelAttribute("organizador") @Valid DadosOrganizador dto, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "organizador/formulario";
		}
		try {
			organizadorService.salvarOuAtualizar(dto);
			redirectAttributes.addFlashAttribute("message", "Organizador salvo com sucesso!");
			return "redirect:/organizador";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
			return "redirect:/organizador";
		}
	}

	@GetMapping("/delete/{id}")
	public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			organizadorService.excluir(id);
			redirectAttributes.addFlashAttribute("message", "Organizador removido!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erro ao excluir.");
		}
		return "redirect:/organizador";
	}
}