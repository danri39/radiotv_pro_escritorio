package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "agencia")
public class Agencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agenciaId;

    private String razaoSocial;

    private String nomeFantasia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipoPessoa;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String cnpj;

    private String rg;

    private String inscricao;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    private String telefone;

    private String celular;

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

    private String banco;

    private String agencia;

    private String conta;

    private int comissaoVendas;

    private BigDecimal vendasMes;

    private BigDecimal comissaoMes;

    private Boolean contratosValidos;

    @Builder.Default
    private Boolean ativo = true;
}
