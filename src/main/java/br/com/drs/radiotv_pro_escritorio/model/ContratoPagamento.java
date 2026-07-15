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
@Table(name = "contrato_pagamento")
public class ContratoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ContratoPagamentoId;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorParcela;

    private Boolean faturado;

    private Boolean paga;

    private LocalDate dataPagamentoReal;

    private BigDecimal valorEfetivoPago;

    private String numeroFatura;

    private Boolean ativo;
}
