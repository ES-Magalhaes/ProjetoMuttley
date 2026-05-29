package com.muttley.pessoa;

import com.muttley.inscricao.InscricaoRepository;
import com.muttley.medalha.MedalhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private MedalhaService medalhaService;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @GetMapping("/{id}")
    public String exibirPerfil(@PathVariable Long id, Model model) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante não encontrado."));

        model.addAttribute("pessoa", pessoa);
        model.addAttribute("medalhas", medalhaService.listarPorParticipante(id));
        model.addAttribute("totalHoras", medalhaService.totalHorasPorParticipante(id));
        model.addAttribute("totalMedalhas", medalhaService.totalMedalhasPorParticipante(id));
        model.addAttribute("historico", inscricaoRepository.findByParticipanteId(id));

        return "perfil/perfil";
    }
}
