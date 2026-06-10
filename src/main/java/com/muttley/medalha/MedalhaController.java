package com.muttley.medalha;

import com.muttley.pessoa.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/medalhas", "/medalha"})
public class MedalhaController {

    @Autowired
    private MedalhaService medalhaService;

    @Autowired
    private MedalhaRepository medalhaRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    // 1. LISTAGEM: Exibe todas as medalhas para gerenciamento
    @GetMapping
    public String listarTodas(Model model) {
        model.addAttribute("medalhas", medalhaRepository.findAll());
        model.addAttribute("participante", Map.of("nome", "Painel de Moderação de Insígnias"));
        return "medalha/listagem";
    }

    @GetMapping("/participante/{id}")
    public String visualizarMedalhas(@PathVariable("id") Long id, Model model) {
        model.addAttribute("participante", pessoaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id)));
        model.addAttribute("medalhas", medalhaService.listarPorParticipante(id));
        return "medalha/listagem";
    }

    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        Medalha medalha = new Medalha();
        medalha.setTipo("HONRA_MANUAL");
        model.addAttribute("medalha", medalha);
        return "medalha/formulario";
    }

    @PostMapping("/salvar")
    public String salvarMedalhaManual(@ModelAttribute("medalha") Medalha medalha) {
        medalha.setDataConquista(LocalDateTime.now());
        medalha.setStatus("PENDENTE"); // Toda medalha nova nasce aguardando aprovação
        medalhaService.concederMedalhaManual(medalha);
        return "redirect:/medalha";
    }

    // ==========================================
    //  NOVAS ROTAS: MODERAÇÃO INDIVIDUAL
    // ==========================================

    @PostMapping("/aprovar/{id}")
    public String aprovarMedalha(@PathVariable("id") Long id) {
        Medalha medalha = medalhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medalha inválida: " + id));
        medalha.setStatus("APROVADA");
        medalhaRepository.save(medalha);
        return "redirect:/medalha";
    }

    @PostMapping("/reprovar/{id}")
    public String reprovarMedalha(@PathVariable("id") Long id) {
        Medalha medalha = medalhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medalha inválida: " + id));
        medalha.setStatus("REPROVADA");
        medalhaRepository.save(medalha);
        return "redirect:/medalha";
    }

    // ==========================================
    //  NOVA ROTA: PROCESSAMENTO EM LOTE (BULK)
    // ==========================================
    
    @PostMapping("/em-lote")
    public String processarEmLote(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam("acao") String acao) {
        
        // Se o usuário não selecionou nenhuma checkbox, apenas ignora e recarrega
        if (ids != null && !ids.isEmpty()) {
            List<Medalha> medalhas = medalhaRepository.findAllById(ids);
            
            for (Medalha medalha : medalhas) {
                if ("aprovar".equalsIgnoreCase(acao)) {
                    medalha.setStatus("APROVADA");
                } else if ("reprovar".equalsIgnoreCase(acao)) {
                    medalha.setStatus("REPROVADA");
                }
            }
            // Salva todas as alterações de uma vez só
            medalhaRepository.saveAll(medalhas);
        }
        
        return "redirect:/medalha";
    }
}