package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;

    @Column(nullable = false, length = 150)
    private String razaoSocial;

    @Column(nullable = false, length = 150)
    private String nomeFantasia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPessoa tipoPessoa;

    @Column(unique = true, length = 18)
    private String cpf;

    @Column(unique = true, length = 18)
    private String cnpj;

    @Column(length = 20)
    private String rg;

    @Column(length = 20)
    private String inscricao;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 21)
    private String celular;

    private String pessoaContato;

    @Column(length = 9)
    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    @Column(length = 2)
    private String estado;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInauguracao;

    private Boolean contratosValidos;

    private Long ramoAtividade;

    @Builder.Default
    private Boolean ativo = true;
}
