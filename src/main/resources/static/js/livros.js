const API_URL = "http://localhost:8080/SistemaBiblioteca/livros";
const API_AUTORES = "http://localhost:8080/SistemaBiblioteca/autores";

const tabela = document.getElementById("tabela-livros");
const form = document.getElementById("form-livro");

const inputId = document.getElementById("livro-id");
const inputTitulo = document.getElementById("titulo");
const inputAno = document.getElementById("ano-publicacao");
const inputIsbn = document.getElementById("isbn");
const inputStatus = document.getElementById("status");
const selectAutor = document.getElementById("autor");

function carregarLivros() {
  fetch(API_URL)
    .then((res) => res.json())
    .then((livros) => {
      tabela.innerHTML = "";

      livros.forEach((livro) => {
        tabela.innerHTML += `
          <tr>
            <td>${livro.id}</td>
            <td>${livro.titulo}</td>
            <td>${livro.anoPublicacao ?? ""}</td>
            <td>${livro.isbn ?? ""}</td>
            <td>${livro.status}</td>
            <td>${livro.nomeAutor}</td>
            <td>
              <button class="btn btn-sm btn-warning"
                onclick="editarLivro(${livro.id})">
                <i class="bi bi-pencil-square"></i>Editar
              </button>

              <button class="btn btn-sm btn-danger"
                onclick="excluirLivro(${livro.id})">
                <i class="bi bi-trash"></i>Excluir
              </button>
            </td>
          </tr>
        `;
      });
    })
    .catch((err) => console.error("Erro ao carregar livros:", err));
}

function editarLivro(id) {
  fetch(`${API_URL}?id=${id}`)
    .then((res) => res.json())
    .then((livro) => {
      prepararEdicao(
        livro.id,
        livro.titulo,
        livro.anoPublicacao,
        livro.isbn,
        livro.status,
        livro.idAutor,
      );
    });
}

function prepararEdicao(id, titulo, ano, isbn, status, idAutor) {
  inputId.value = id;
  inputTitulo.value = titulo;
  inputAno.value = ano;
  inputIsbn.value = isbn;
  inputStatus.value = status;
  selectAutor.value = idAutor;
}

function carregarAutores() {
  fetch(API_AUTORES)
    .then((res) => res.json())
    .then((autores) => {
      selectAutor.innerHTML = "";

      autores.forEach((autor) => {
        selectAutor.innerHTML += `
          <option value="${autor.id}">${autor.nome}</option>
        `;
      });
    });
}

form.addEventListener("submit", (e) => {
  e.preventDefault();

  const livro = {
    titulo: inputTitulo.value,
    anoPublicacao: Number(inputAno.value),
    isbn: inputIsbn.value,
    status: inputStatus.value,
    idAutor: Number(selectAutor.value),
  };

  if (inputId.value) {
    atualizarLivro(inputId.value, livro);
  } else {
    cadastrarLivro(livro);
  }
});

function cadastrarLivro(livro) {
  fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(livro),
  }).then(() => {
    form.reset();
    carregarLivros();
  });
}

function atualizarLivro(id, livro) {
  fetch(`${API_URL}?id=${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(livro),
  }).then(() => {
    cancelarEdicao();
    carregarLivros();
  });
}

function excluirLivro(id) {
  if (!confirm("Deseja excluir este livro?")) return;

  fetch(`${API_URL}?id=${id}`, { method: "DELETE" }).then(() =>
    carregarLivros(),
  );
}

function cancelarEdicao() {
  inputId.value = "";
  form.reset();
}

carregarAutores();
carregarLivros();
