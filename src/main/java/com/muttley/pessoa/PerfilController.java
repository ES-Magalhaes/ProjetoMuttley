package com.muttley.pessoa;

import com.muttley.inscricao.InscricaoRepository;
import com.muttley.medalha.Medalha;
import com.muttley.medalha.MedalhaService;
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
    private InscricaoRepository inscricaoRepository;

    @GetMapping("/{id}")
    public String exibirPerfil(@PathVariable Long id, Model model) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante não encontrado."));

        // Apenas medalhas validadas
        List<Medalha> medalhasValidadas = medalhaService.listarPorParticipante(id).stream()
                .filter(Medalha::isValidada)
                .toList();

        model.addAttribute("pessoa", pessoa);
        model.addAttribute("medalhas", medalhasValidadas);
        model.addAttribute("totalHoras", medalhaService.totalHorasPorParticipante(id));
        model.addAttribute("totalMedalhas", medalhasValidadas.size());
        model.addAttribute("historico", inscricaoRepository.findByParticipanteId(id));

        return "perfil/perfil";
    }
}
