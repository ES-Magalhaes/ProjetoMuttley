package com.muttley.pessoa;

import com.muttley.inscricao.InscricaoRepository;
import com.muttley.medalha.Medalha;
import com.muttley.medalha.MedalhaService;
import com.muttley.medalha.MedalhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private MedalhaService medalhaService;

    @Autowired
    private MedalhaRepository medalhaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @GetMapping("/{id}")
    public String exibirPerfil(@PathVariable Long id, Model model) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante não encontrado."));

        // Busca todas as medalhas conquistadas
        List<Medalha> medalhas = medalhaService.listarPorParticipante(id);

        // Calcula o total de horas em eventos com check-in
        int totalHoras = medalhaRepository.sumCargaHorariaPorParticipante(id);

        model.addAttribute("pessoa", pessoa);
        model.addAttribute("medalhas", medalhas);
        model.addAttribute("totalHoras", totalHoras);
        model.addAttribute("totalMedalhas", medalhas.size());
        model.addAttribute("historico", inscricaoRepository.findByParticipanteId(id));

        return "perfil/perfil";
    }
}