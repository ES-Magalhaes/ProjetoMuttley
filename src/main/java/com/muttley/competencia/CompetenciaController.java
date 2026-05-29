package com.muttley.competencia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/competencia")
public class CompetenciaController {

    @Autowired
    private CompetenciaRepository repository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("competencias", repository.findAll());
        model.addAttribute("tipos", Competencia.TipoCompetencia.values());
        return "competencia/listagem";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("competencia", new Competencia());
        model.addAttribute("tipos", Competencia.TipoCompetencia.values());
        return "competencia/formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("competencia", repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competência não encontrada.")));
        model.addAttribute("tipos", Competencia.TipoCompetencia.values());
        return "competencia/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Competencia competencia) {
        repository.save(competencia);
        return "redirect:/competencia";
    }

    @GetMapping("/delete/{id}")
    public String excluir(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/competencia";
    }
}
