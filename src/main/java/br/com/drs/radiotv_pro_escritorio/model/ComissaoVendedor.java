package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusComissao;
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
@Table(name = "comissao_vendedor")
public class ComissaoVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comissaoVendedorId;

    // ==========================================
    // VÍNCULOS
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_pagamento_id", nullable = false)
    private ContratoPagamento contratoPagamento;

    // ==========================================
    // DADOS DA COMISSÃO
    // ==========================================

    // Mês de referência da comissão (ex: "07/2026")
    // Agrupa as comissões para processamento em lote na folha
    @Column(nullable = false, length = 7)
    private String mesReferencia;

    // Valor da comissão calculado (% sobre o valor efetivamente recebido do cliente)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorComissao;

    // Percentual aplicado (ex: 10 para 10%)
    @Column(nullable = false)
    private Integer percentualAplicado;

    // Data em que a comissão foi calculada (quando o cliente pagou)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataCalculo;

    // ==========================================
    // CONTROLE DE PROCESSAMENTO NA FOLHA
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusComissao statusComissao = StatusComissao.PENDENTE;

    // Data em que foi processada na folha (null enquanto PENDENTE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataProcessamento;

    // ID da folha de pagamento onde foi processada (null enquanto PENDENTE)
    private Long folhaPagamentoId;

    @Builder.Default
    private Boolean ativa = true;

    // ==========================================
    // MÉTODO HELPER
    // ==========================================

    /**
     * Marca a comissão como PROCESSADA na folha de pagamento.
     * Chamado pelo FolhaPagamentoService quando o salário é fechado.
     */
    public void processarNaFolha(Long folhaPagamentoId) {
        if (this.statusComissao == StatusComissao.PROCESSADA) {
            throw new IllegalStateException("Esta comissão já foi processada na folha.");
        }
        if (this.statusComissao == StatusComissao.CANCELADA) {
            throw new IllegalStateException("Não é possível processar uma comissão cancelada.");
        }

        this.statusComissao = StatusComissao.PROCESSADA;
        this.dataProcessamento = LocalDate.now();
        this.folhaPagamentoId = folhaPagamentoId;
    }

    /**
     * Cancela a comissão (ex: quando a parcela é estornada).
     */
    public void cancelar() {
        if (this.statusComissao == StatusComissao.PROCESSADA) {
            throw new IllegalStateException("Não é possível cancelar uma comissão já processada na folha.");
        }
        this.statusComissao = StatusComissao.CANCELADA;
        this.ativa = false;
    }
}