package com.muttley.pessoa;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    // 1. Listagem completa
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pessoas", pessoaService.listarTodos());
        return "pessoa/listagem";
    }

    // 2. Abertura do formulário (vazio para novo registro)
    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("pessoa", new DadosPessoa(null, "", "", "", "", "", ""));
        return "pessoa/formulario";
    }

    // 3. Edição (carrega dados de uma pessoa existente)
    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pessoa", pessoaService.buscarParaEdicao(id));
        return "pessoa/formulario";
    }

    // 4. Salvar ou Atualizar
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("pessoa") DadosPessoa dto,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "pessoa/formulario";
        }
        try {
            pessoaService.salvarOuAtualizar(dto);
            return "redirect:/pessoa";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "pessoa/formulario";
        }
    }

    // 5. Exclusão
    @GetMapping("/delete/{id}")
    public String excluir(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            pessoaService.excluir(id);
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pessoa";
    }

    // 6. Busca por CPF (usado pelo formulário de inscrição via fetch/AJAX)
    @GetMapping("/buscar-cpf")
    @ResponseBody
    public ResponseEntity<DadosPessoa> buscarPorCpf(@RequestParam String cpf) {
        // Remove formatação caso venha com máscara
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return pessoaService.buscarPorCpf(cpfLimpo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}