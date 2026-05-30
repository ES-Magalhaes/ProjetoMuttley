package com.muttley.competencia;

import com.muttley.evento.Evento;
import com.muttley.evento.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/competencia")
public class CompetenciaController {

    @Autowired
    private CompetenciaRepository repository;

    @Autowired
    private EventoRepository eventoRepository;

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
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            // Verifica se a competência está vinculada a algum evento
            Competencia competencia = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Competência não encontrada."));

            List<Evento> eventosVinculados = eventoRepository.findByCompetenciasContaining(competencia);
            if (!eventosVinculados.isEmpty()) {
                String nomes = eventosVinculados.stream()
                        .map(Evento::getNome)
                        .collect(Collectors.joining(", "));
                throw new RuntimeException("Não é possível excluir esta competência. "
                        + "Ela está vinculada aos eventos: " + nomes
                        + ". Remova o vínculo nos eventos antes de excluir.");
            }

            repository.deleteById(id);
            ra.addFlashAttribute("sucesso", "Competência excluída com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/competencia";
    }
}
