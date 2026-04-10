package com.muttley.aluno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoMapper alunoMapper;

    // Listagem de alunos
    @GetMapping
    public String listarAlunos(Model model) {
        model.addAttribute("listaAlunos", alunoService.procurarTodos());
        return "aluno/listagem";
    }

    // Abre o formulário (Novo ou Edição via Query Param ?ra=1)
    @GetMapping("/formulario")
    public String mostrarFormulario(@RequestParam(required = false) Long ra, Model model) {
        AtualizacaoAluno dto;
        if (ra != null) {
            Aluno aluno = alunoService.procurarPorRA(ra)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
            dto = alunoMapper.toDTO(aluno);
        } else {
            // Cria um record vazio para o formulário de inserção
            dto = new AtualizacaoAluno(null, "", "", 0);
        }
        model.addAttribute("aluno", dto);
        return "aluno/formulario";
    }

    // Abre o formulário de edição via Path Variable (/aluno/formulario/1)
    @GetMapping("/formulario/{ra}")
    public String carregarFormularioPorPath(@PathVariable("ra") Long ra, Model model, RedirectAttributes redirectAttributes) {
        try {
            Aluno aluno = alunoService.procurarPorRA(ra)
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
            model.addAttribute("aluno", alunoMapper.toDTO(aluno));
            return "aluno/formulario";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/aluno";
        }
    }

    // Processa o salvamento ou atualização
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("aluno") @Valid AtualizacaoAluno dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        if (result.hasErrors()) {
            return "aluno/formulario";
        }
        
        try {
            alunoService.salvarOuAtualizar(dto);
            String mensagem = (dto.ra() != null) 
                ? "Aluno '" + dto.nome() + "' atualizado com sucesso!"
                : "Aluno '" + dto.nome() + "' cadastrado com sucesso!";
            
            redirectAttributes.addFlashAttribute("message", mensagem);
            return "redirect:/aluno";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar: " + e.getMessage());
            return "redirect:/aluno";
        }
    }

    @GetMapping("/delete/{ra}")
    public String deletar(@PathVariable("ra") Long ra, RedirectAttributes redirectAttributes) {
        try {
            alunoService.apagarPorRA(ra);
            redirectAttributes.addFlashAttribute("message", "Aluno removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao deletar: " + e.getMessage());
        }
        return "redirect:/aluno";
    }
}