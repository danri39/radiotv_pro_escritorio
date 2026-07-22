package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
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
    private Long contratoPagamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    // Número sequencial da parcela (ex: 1 de 12, 2 de 12...)
    @Column(nullable = false)
    private Integer numeroParcela;

    // Data de vencimento da parcela
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataVencimento;

    // Valor original da parcela (ex: R$ 1.500,00)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcela;

    // Status da parcela (substitui os booleans faturado/paga)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusRecebimento statusRecebimento = StatusRecebimento.A_FATURAR;

    // Número da fatura/boleto gerado (preenchido quando FATURADO)
    private String numeroFatura;

    // Data em que o cliente efetivamente pagou (preenchido quando RECEBIDO)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamentoReal;

    // Valor que o cliente realmente pagou (pode ter multa/juros ou desconto)
    @Column(precision = 10, scale = 2)
    private BigDecimal valorEfetivoPago;

    // ==========================================
    // CONTROLE DE COMISSÕES (Trava contra duplicidade)
    // ==========================================

    // true = a comissão do vendedor já foi lançada na folha
    @Builder.Default
    private Boolean comissaoVendedorLancada = false;

    // true = a comissão da agência já foi lançada em Pagamentos
    @Builder.Default
    private Boolean comissaoAgenciaLancada = false;

    @Builder.Default
    private Boolean ativo = true;

    // ==========================================
    // MÉTODO HELPER: Baixa de Pagamento
    // ==========================================

    /**
     * Marca a parcela como RECEBIDA.
     * Este método é chamado pelo ContratoPagamentoService quando o escritório dá baixa.
     * Após isso, o Service deve disparar o cálculo das comissões (vendedor + agência).
     */
    public void marcarComoRecebido(LocalDate dataPagamento, BigDecimal valorRecebido) {
        if (this.statusRecebimento == StatusRecebimento.RECEBIDO) {
            throw new IllegalStateException("Esta parcela já foi recebida.");
        }
        if (this.statusRecebimento == StatusRecebimento.CANCELADO) {
            throw new IllegalStateException("Não é possível receber uma parcela cancelada.");
        }

        this.statusRecebimento = StatusRecebimento.RECEBIDO;
        this.dataPagamentoReal = dataPagamento;
        this.valorEfetivoPago = valorRecebido;
    }

    /**
     * Marca a parcela como FATURADA (boleto/nota emitida).
     */
    public void marcarComoFaturado(String numeroFatura) {
        if (this.statusRecebimento != StatusRecebimento.A_FATURAR) {
            throw new IllegalStateException("Apenas parcelas A_FATURAR podem ser faturadas.");
        }
        this.statusRecebimento = StatusRecebimento.FATURADO;
        this.numeroFatura = numeroFatura;
    }

    /**
     * Marca a parcela como ATRASADA.
     * Geralmente chamado por uma tarefa agendada (job) que roda diariamente.
     */
    public void marcarComoAtrasado() {
        if (this.statusRecebimento == StatusRecebimento.A_FATURAR
                || this.statusRecebimento == StatusRecebimento.FATURADO) {
            if (this.dataVencimento.isBefore(LocalDate.now())) {
                this.statusRecebimento = StatusRecebimento.ATRASADO;
            }
        }
    }
}