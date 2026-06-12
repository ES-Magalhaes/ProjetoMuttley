package com.muttley.medalha;

import com.muttley.evento.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medalha/regras")
public class RegraMedalhaController {

    @Autowired
    private RegraMedalhaRepository regraMedalhaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    // Listagem de todas as regras cadastradas
    @GetMapping
    public String listarRegras(Model model) {
        model.addAttribute("regras", regraMedalhaRepository.findAll());
        return "medalha/regras-listagem";
    }

    // Formulário de criação de nova regra
    @GetMapping("/nova")
    public String exibirFormulario(Model model) {
        model.addAttribute("regra", new RegraMedalha());
        model.addAttribute("tiposCriterio", TipoCriterio.values());
        model.addAttribute("eventos", eventoRepository.findAll());
        return "medalha/regras-formulario";
    }

    // Formulário de edição de regra existente
    @GetMapping("/editar/{id}")
    public String editarRegra(@PathVariable("id") Long id, Model model) {
        RegraMedalha regra = regraMedalhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regra inválida: " + id));
        model.addAttribute("regra", regra);
        model.addAttribute("tiposCriterio", TipoCriterio.values());
        model.addAttribute("eventos", eventoRepository.findAll());
        return "medalha/regras-formulario";
    }

    // Salvar (criação ou atualização)
    @PostMapping("/salvar")
    public String salvarRegra(@ModelAttribute("regra") RegraMedalha regra,
                              @RequestParam(value = "eventoId", required = false) Long eventoId) {
        if (eventoId != null) {
            regra.setEvento(eventoRepository.findById(eventoId).orElse(null));
        } else {
            regra.setEvento(null);
        }
        regraMedalhaRepository.save(regra);
        return "redirect:/medalha/regras";
    }

    // Ativar regra
    @PostMapping("/ativar/{id}")
    public String ativarRegra(@PathVariable("id") Long id) {
        RegraMedalha regra = regraMedalhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regra inválida: " + id));
        regra.setAtivo(true);
        regraMedalhaRepository.save(regra);
        return "redirect:/medalha/regras";
    }

    // Desativar regra
    @PostMapping("/desativar/{id}")
    public String desativarRegra(@PathVariable("id") Long id) {
        RegraMedalha regra = regraMedalhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regra inválida: " + id));
        regra.setAtivo(false);
        regraMedalhaRepository.save(regra);
        return "redirect:/medalha/regras";
    }

    // Excluir regra
    @PostMapping("/excluir/{id}")
    public String excluirRegra(@PathVariable("id") Long id) {
        regraMedalhaRepository.deleteById(id);
        return "redirect:/medalha/regras";
    }
}
