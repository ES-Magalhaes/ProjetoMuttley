package com.muttley.pessoa;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.muttley.inscricao.Inscricao;

@Entity
@Table(name = "pessoas")
@Inheritance(strategy = InheritanceType.JOINED) // Permite que Organizador herde esta tabela no banco
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    // Dados específicos do participante/aluno exigidos pelo escopo
    private String ra;
    private String curso;
    private String telefone;

    // Relacionamento um-para-múltos com a nova entidade intermediária de Inscrição
    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inscricao> inscricoes = new ArrayList<>();

    // Retorna o CPF formatado (000.000.000-00) para exibição nas telas
    // O banco sempre armazena sem formatação
    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." +
               cpf.substring(3, 6) + "." +
               cpf.substring(6, 9) + "-" +
               cpf.substring(9);
    }
}