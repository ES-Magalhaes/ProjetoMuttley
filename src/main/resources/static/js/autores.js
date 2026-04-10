const API_URL = "http://localhost:8080/SistemaBiblioteca/autores";

const tabela = document.getElementById("tabela-autores");
const form = document.getElementById("form-autor");

const inputId = document.getElementById("autor-id");
const inputNome = document.getElementById("nome");
const inputNacionalidade = document.getElementById("nacionalidade");
const inputEstilo = document.getElementById("estilo");

function carregarAutores() {
  fetch(API_URL)
    .then((res) => res.json())
    .then((autores) => {
      tabela.innerHTML = "";

      autores.forEach((autor) => {
        tabela.innerHTML += `
          <tr>
            <td>${autor.id}</td>
            <td>${autor.nome}</td>
            <td>${autor.nacionalidade}</td>
            <td>${autor.estilo}</td>
            <td class="d-flex gap-2">
              <button
                class="btn btn-warning btn-sm"
                title="Editar"
                onclick="prepararEdicao(
                  ${autor.id},
                  '${autor.nome}',
                  '${autor.nacionalidade}',
                  '${autor.estilo}'
                )"
              >
                <i class="bi bi-pencil-square"></i>Editar
              </button>

              <button
                class="btn btn-danger btn-sm"
                title="Excluir"
                onclick="excluirAutor(${autor.id})"
              >
                <i class="bi bi-trash"></i>Excluir
              </button>
            </td>
          </tr>
        `;
      });
    })
    .catch((error) => console.error("Erro ao carregar autores:", error));
}

form.addEventListener("submit", (e) => {
  e.preventDefault();

  const autor = {
    nome: inputNome.value,
    nacionalidade: inputNacionalidade.value,
    estilo: inputEstilo.value,
  };

  if (inputId.value) {
    atualizarAutor(inputId.value, autor);
  } else {
    cadastrarAutor(autor);
  }
});

function cadastrarAutor(autor) {
  fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(autor),
  }).then(() => {
    form.reset();
    carregarAutores();
  });
}

function prepararEdicao(id, nome, nacionalidade, estilo) {
  inputId.value = id;
  inputNome.value = nome;
  inputNacionalidade.value = nacionalidade;
  inputEstilo.value = estilo;
}

function atualizarAutor(id, autor) {
  fetch(`${API_URL}?id=${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(autor),
  }).then(() => {
    cancelarEdicao();
    carregarAutores();
  });
}

function excluirAutor(id) {
  if (!confirm("Tem certeza que deseja excluir este autor?")) return;

  fetch(`${API_URL}?id=${id}`, { method: "DELETE" }).then(() =>
    carregarAutores(),
  );
}

function cancelarEdicao() {
  inputId.value = "";
  form.reset();
}

carregarAutores();
