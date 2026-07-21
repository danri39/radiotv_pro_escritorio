package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusFolha;
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

    // ==========================================
    // VÍNCULOS
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    // Mês de referência da folha (formato "MM/yyyy", ex: "07/2026")
    @Column(nullable = false, length = 7)
    private String mesReferencia;

    // ==========================================
    // DATAS
    // ==========================================

    // Data em que a folha foi fechada/gerada pelo sistema
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataFechamento;

    // Data em que o salário foi efetivamente pago ao funcionário
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    // Competência (mês de trabalho - geralmente igual ao mesReferencia)
    @Column(nullable = false, length = 7)
    private String competencia;

    // ==========================================
    // PROVENTOS (O que o funcionário RECEBE)
    // ==========================================

    // Salário base do funcionário (vem do Funcionario.salarioBruto)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salarioBruto;

    // Total de comissões do vendedor processadas neste mês
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalComissoes = BigDecimal.ZERO;

    // Total de proventos = salarioBruto + totalComissoes + outros proventos
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalProventos = BigDecimal.ZERO;

    // ==========================================
    // DESCONTOS (O que o funcionário PERDE)
    // ==========================================

    // Total de benefícios (plano de saúde, odontológico, coparticipações)
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalBeneficios = BigDecimal.ZERO;

    // Outros descontos (INSS, IR, vale-transporte, vale-refeição, adiantamentos)
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalOutrosDescontos = BigDecimal.ZERO;

    // Total de todos os descontos
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalDescontos = BigDecimal.ZERO;

    // ==========================================
    // VALOR FINAL
    // ==========================================

    // Salário líquido = totalProventos - totalDescontos
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal salarioLiquido = BigDecimal.ZERO;

    // ==========================================
    // STATUS E CONTROLE
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusFolha statusFolha = StatusFolha.ABERTA;

    // ID do pagamento gerado no módulo de Pagamentos (quando a folha é paga)
    private Long pagamentoId;

    // Forma de pagamento (TED, PIX, dinheiro, etc.)
    private String formaPagamento;

    @Column(length = 1000)
    private String observacao;

    @Builder.Default
    private Boolean ativa = true;

    // ==========================================
    // MÉTODOS HELPERS
    // ==========================================

    /**
     * Recalcula todos os totais da folha.
     * Deve ser chamado sempre que algum valor for alterado.
     */
    public void recalcularTotais() {
        // Total de proventos = salário + comissões
        this.totalProventos = this.salarioBruto.add(this.totalComissoes);

        // Total de descontos = benefícios + outros descontos
        this.totalDescontos = this.totalBeneficios.add(this.totalOutrosDescontos);

        // Salário líquido = proventos - descontos
        this.salarioLiquido = this.totalProventos.subtract(this.totalDescontos);
    }

    /**
     * Fecha a folha (não pode mais ser editada).
     */
    public void fechar() {
        if (this.statusFolha != StatusFolha.ABERTA) {
            throw new IllegalStateException("Apenas folhas ABERTAS podem ser fechadas.");
        }
        recalcularTotais();
        this.statusFolha = StatusFolha.FECHADA;
    }

    /**
     * Marca a folha como PAGA.
     */
    public void marcarComoPaga(LocalDate dataPagamento, String formaPagamento, Long pagamentoId) {
        if (this.statusFolha != StatusFolha.FECHADA) {
            throw new IllegalStateException("Apenas folhas FECHADAS podem ser pagas.");
        }
        this.statusFolha = StatusFolha.PAGA;
        this.dataPagamento = dataPagamento;
        this.formaPagamento = formaPagamento;
        this.pagamentoId = pagamentoId;
    }

    /**
     * Cancela a folha (não pode ser usada se já estiver paga).
     */
    public void cancelar() {
        if (this.statusFolha == StatusFolha.PAGA) {
            throw new IllegalStateException("Não é possível cancelar uma folha já paga.");
        }
        this.statusFolha = StatusFolha.CANCELADA;
        this.ativa = false;
    }
}