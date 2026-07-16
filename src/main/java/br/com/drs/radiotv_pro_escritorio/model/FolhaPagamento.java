package br.com.drs.radiotv_pro_escritorio.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "folha_pagamento")
public class FolhaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folhaPagamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false, length = 7)
    private String mesAno;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBruto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal comissao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal outrosPagamentos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descontoInss;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descontoIrrf;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descontoBeneficios; // Soma de (Mensalidade + Coparticipação) de todos os dependentes e titular

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDescontos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioLiquido;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    @Builder.Default
    private Boolean fechada = false;
}
