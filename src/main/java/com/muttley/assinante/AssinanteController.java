package com.muttley.assinante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/assinante")
public class AssinanteController {

    @Autowired
    private AssinanteRepository assinanteRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("assinantes", assinanteRepository.findAll());
        return "assinante/listagem";
    }

    @GetMapping("/formulario")
    public String novoFormulario(Model model) {
        model.addAttribute("assinante", new Assinante());
        return "assinante/formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Assinante assinante = assinanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assinante inválido: " + id));
        model.addAttribute("assinante", assinante);
        return "assinante/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Assinante assinante) {
        assinanteRepository.save(assinante);
        return "redirect:/assinante";
    }

    @GetMapping("/delete/{id}")
    public String excluir(@PathVariable Long id) {
        assinanteRepository.deleteById(id);
        return "redirect:/assinante";
    }
}
