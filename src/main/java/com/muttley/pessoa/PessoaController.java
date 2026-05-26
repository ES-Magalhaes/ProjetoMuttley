package com.muttley.pessoa;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
        // Certifique-se que o serviço retorna List<DadosPessoa>
        model.addAttribute("pessoas", pessoaService.listarTodos());
        return "pessoa/listagem";
    }

    // 2. Abertura do formulário (vazio para novo registro)
    @GetMapping("/formulario")
    public String formulario(Model model) {
        // Inicializa o DTO com campos vazios
        model.addAttribute("pessoa", new DadosPessoa(null, "", "", "", "", "", ""));
        return "pessoa/formulario";
    }

    // 3. Edição (carrega dados de uma pessoa existente)
    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        // O serviço retorna o DTO pronto para preencher os campos
        model.addAttribute("pessoa", pessoaService.buscarParaEdicao(id));
        return "pessoa/formulario";
    }

    // 4. Salvar ou Atualizar
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("pessoa") DadosPessoa dto,
            BindingResult result,
            Model model) {

        // Verifica se houve erros de validação no formulário
        if (result.hasErrors()) {
            return "pessoa/formulario";
        }

        try {
            pessoaService.salvarOuAtualizar(dto);
            return "redirect:/pessoa"; // Redireciona para a listagem em caso de sucesso
        } catch (Exception e) {
            // Se o CPF já existir ou houver erro de banco, captura a mensagem e mostra no
            // form
            model.addAttribute("erro", e.getMessage());
            return "pessoa/formulario";
        }
    }

    // 5. Exclusão
    @GetMapping("/delete/{id}")
    public String excluir(@PathVariable Long id) {
        pessoaService.excluir(id);
        return "redirect:/pessoa";
    }
}