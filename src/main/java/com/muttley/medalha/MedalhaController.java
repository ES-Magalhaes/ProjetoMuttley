package com.muttley.medalha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medalha")
public class MedalhaController {

    @Autowired
    private MedalhaService medalhaService;

    // GET /medalha — listagem geral com filtro
    @GetMapping
    public String listar(@RequestParam(required = false, defaultValue = "todas") String filtro,
                         Model model) {
        if ("pendentes".equals(filtro)) {
            model.addAttribute("medalhas", medalhaService.listarPendentes());
        } else {
            model.addAttribute("medalhas", medalhaService.listarTodasOrdenadas());
        }
        model.addAttribute("filtroAtivo", filtro);
        return "medalha/listagem";
    }

    // POST /medalha/validar/{id} — valida a medalha
    @PostMapping("/validar/{id}")
    public String validar(@PathVariable Long id,
                          @RequestParam(required = false, defaultValue = "") String observacao,
                          RedirectAttributes ra) {
        try {
            medalhaService.validar(id, observacao);
            ra.addFlashAttribute("sucesso", "Medalha validada com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/medalha";
    }

    // POST /medalha/revogar/{id} — revoga a validação
    @PostMapping("/revogar/{id}")
    public String revogar(@PathVariable Long id,
                          @RequestParam(required = false, defaultValue = "") String observacao,
                          RedirectAttributes ra) {
        try {
            medalhaService.revogar(id, observacao);
            ra.addFlashAttribute("sucesso", "Validação revogada.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/medalha";
    }

    // GET /medalha/delete/{id} — exclui a medalha
    @GetMapping("/delete/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            medalhaService.excluir(id);
            ra.addFlashAttribute("sucesso", "Medalha removida.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/medalha";
    }
}
