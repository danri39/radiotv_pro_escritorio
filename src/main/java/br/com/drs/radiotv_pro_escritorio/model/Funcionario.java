package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Formacao;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sexo;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long funcionarioId;

    @Column(nullable = false, length = 150)
    private String nome;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    @Enumerated(EnumType.STRING)
    private TipoPessoa tipoPessoa;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String cnpj;

    private String rg;

    private String incricao;

    @Column(length = 11)
    private String pisPasep;

    private String cnh;

    private String categoria;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate vencimentoCNH;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    private String telefone;

    private String celular;

    @Enumerated(EnumType.STRING)
    private Formacao formacao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate admissao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate demissao;

    private String cargo;

    private BigDecimal salarioBruto;

    private BigDecimal salarioLiquido;

    private BigDecimal descontosFixos;

    private String banco;

    private String agencia;

    private String conta;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime entradaServico;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime saidaServico;

    @Builder.Default
    private Boolean vendedor = false;

    @Builder.Default
    private Boolean ativo = true;
}
