package com.muttley.evento;

import com.muttley.pessoa.DadosPessoa;
import com.muttley.pessoa.PessoaService;
import com.muttley.inscricao.InscricaoService;
import com.muttley.competencia.CompetenciaRepository;
import com.muttley.organizador.OrganizadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private EventoService eventoService;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private InscricaoService inscricaoService;

	@Autowired
	private OrganizadorService organizadorService;

	@Autowired
	private CompetenciaRepository competenciaRepository;

	// =========================================================================
	// ROTAS DO CRUD DE EVENTOS (Adicionadas para corrigir o Erro 404)
	// =========================================================================

	// GET /evento - Renderiza a página de listagem
	@GetMapping
	public String listarTodos(Model model) {
		model.addAttribute("eventos", eventoService.listarTodos());
		return "evento/listagem"; // Alinhe com o nome real do seu arquivo (ex: evento/listagem ou evento/index)
	}

	@GetMapping("/formulario")
	public String exibirFormularioNovo(Model model) {
		model.addAttribute("evento", new DadosEvento(null, "", "", "", "", "", null, null, null, null, null, null, null));
		model.addAttribute("organizadores", organizadorService.listarTodos());
		model.addAttribute("todasCompetencias", competenciaRepository.findAll());
		return "evento/formulario";
	}

	@GetMapping("/formulario/{id}")
	public String exibirFormularioEditar(@PathVariable Long id, Model model) {
		try {
			DadosEvento dto = eventoService.buscarParaEdicao(id);
			model.addAttribute("evento", dto);
			model.addAttribute("organizadores", organizadorService.listarTodos());
			model.addAttribute("todasCompetencias", competenciaRepository.findAll());
			// IDs das competências já associadas ao evento
			model.addAttribute("competenciasSelecionadas",
				eventoService.buscarPorId(id).getCompetencias().stream()
					.map(c -> c.getId()).toList());
			return "evento/formulario";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "redirect:/evento";
		}
	}

	// POST /evento/salvar - Recebe os dados do formulário e salva no banco
	@PostMapping("/salvar")
	public String salvarEvento(@Valid @ModelAttribute("evento") DadosEvento dto, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("organizadores", organizadorService.listarTodos());
			return "evento/formulario";
		}
		try {
			// Captura o evento salvo com o ID gerado
			Evento eventoSalvo = eventoService.salvarOuAtualizar(dto);
			// Redireciona para a página do QR Code passando o ID
			return "redirect:/evento/qrcode/" + eventoSalvo.getId();
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("organizadores", organizadorService.listarTodos());
			return "evento/formulario";
		}
	}

	@GetMapping("/qrcode/{id}")
	public String exibirQrCodeDoEvento(@PathVariable Long id, Model model) {
		try {
			Evento evento = eventoService.buscarPorId(id);
			model.addAttribute("evento", evento);
			return "evento/qrcode"; // Nome do novo arquivo HTML
		} catch (Exception e) {
			model.addAttribute("error", "Erro ao carregar o QR Code: " + e.getMessage());
			return "redirect:/evento";
		}
	}

	// GET /evento/delete/{id} - Exclui o evento do sistema
	@GetMapping("/delete/{id}")
	public String excluirEvento(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
		try {
			eventoService.excluir(id);
			ra.addFlashAttribute("message", "Evento excluído com sucesso.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/evento";
	}

	@GetMapping("/inscrever/{id}")
	public String exibirFormularioInscricao(@PathVariable Long id, Model model) {
		try {
			Evento evento = eventoService.buscarPorId(id);
			model.addAttribute("evento", evento);
			model.addAttribute("dadosPessoa", new DadosPessoa(null, "", "", "", "", "", null));
			return "inscricao/formulario-inscricao";
		} catch (Exception e) {
			model.addAttribute("error", "Evento não encontrado.");
			return "redirect:/evento";
		}
	}

	@PostMapping("/inscrever/{id}")
	public String processarInscricao(@PathVariable Long id, DadosPessoa dados, Model model) {
		try {
			String qrCode = pessoaService.inscreverPessoaEmEvento(dados, id);
			model.addAttribute("mensagem", "Inscrição realizada com sucesso!");
			model.addAttribute("qrCode", qrCode);
			return "inscricao/sucesso-inscricao";
		} catch (Exception e) {
			model.addAttribute("erro", e.getMessage());
			model.addAttribute("evento", eventoService.buscarPorId(id));
			return "inscricao/formulario-inscricao";
		}
	}

	@GetMapping("/checkin/{inscricaoId}")
	public String realizarCheckIn(@PathVariable Long inscricaoId, Model model) {
		try {
			inscricaoService.realizarCheckIn(inscricaoId);
			model.addAttribute("mensagem", "Check-in realizado com sucesso!");
			model.addAttribute("status", "OK");
		} catch (Exception e) {
			model.addAttribute("mensagem", "Erro: " + e.getMessage());
			model.addAttribute("status", "ERRO");
		}
		return "inscricao/checkin-inscricao";
	}

	@GetMapping("/participantes/{id}")
	public String listarParticipantes(@PathVariable Long id, Model model) {
		try {
			Evento evento = eventoService.buscarPorId(id);
			model.addAttribute("evento", evento);
			model.addAttribute("inscricoes", inscricaoService.listarPorEvento(id));
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "redirect:/evento";
		}
		return "evento/participantes";
	}
}