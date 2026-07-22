package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;
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
@Table(name = "pagamentos")
public class Pagamentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pagamentoId;

    // ==========================================
    // CLASSIFICAÇÃO DO PAGAMENTO
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPagamento tipoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    // Descrição livre para identificação (ex: "Comissão Agência X - Parcela 3/12", "Conta de Luz Julho/2026")
    @Column(nullable = false)
    private String descricao;

    // Nome de quem vai receber (ex: nome da agência, fornecedor, funcionário)
    @Column(nullable = false)
    private String beneficiario;

    // ==========================================
    // VALORES E DATAS
    // ==========================================

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataVencimento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento; // Preenchido quando PAGO

    // Forma de pagamento (PIX, TED, DOC, boleto, dinheiro, etc.)
    private String formaPagamento;

    // ==========================================
    // DOCUMENTO DA AGÊNCIA (Trava de segurança)
    // ==========================================

    // Número da NF/Boleto informado pela agência no upload
    private String numeroDocumento;

    // Caminho do arquivo uploadado pela agência (PDF, imagem, etc.)
    private String caminhoArquivoDocumento;

    // Data em que a agência fez o upload do documento
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataUploadDocumento;

    // ==========================================
    // VÍNCULOS OPCIONAIS (apenas UM será preenchido, dependendo do tipo)
    // ==========================================

    // Para COMISSAO_AGENCIA: vínculo com a agência
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    // Para COMISSAO_AGENCIA: vínculo com a parcela do contrato que gerou a comissão
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_pagamento_id")
    private ContratoPagamento contratoPagamento;

    // Para COMPRA_APROVADA: vínculo com a compra
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")
    private Compras compra;

    // Para SALARIO, VALE, COMISSAO_VENDEDOR: vínculo com o funcionário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    // ==========================================
    // CONTROLE
    // ==========================================

    @Column(length = 1000)
    private String observacao;

    @Builder.Default
    private Boolean ativo = true;

    // ==========================================
    // MÉTODOS HELPERS
    // ==========================================

    /**
     * Registra o upload do documento feito pela agência.
     * Muda o status de AGUARDANDO_DOCUMENTO para PRONTO_PARA_PAGAMENTO.
     */
    public void registrarDocumentoAgencia(String numeroDocumento, String caminhoArquivo) {
        if (this.statusPagamento != StatusPagamento.AGUARDANDO_DOCUMENTO) {
            throw new IllegalStateException(
                    "Apenas pagamentos AGUARDANDO_DOCUMENTO podem receber upload. Status atual: "
                            + this.statusPagamento.getDescricao()
            );
        }
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("O número do documento é obrigatório.");
        }
        this.numeroDocumento = numeroDocumento;
        this.caminhoArquivoDocumento = caminhoArquivo;
        this.dataUploadDocumento = LocalDate.now();
        this.statusPagamento = StatusPagamento.PRONTO_PARA_PAGAMENTO;
    }

    /**
     * Marca o pagamento como PAGO.
     * Para comissões de agência, EXIGE que o documento já tenha sido registrado.
     */
    public void marcarComoPago(LocalDate dataPagamento, String formaPagamento) {
        // TRAVA: Comissão de agência só pode ser paga se tiver documento
        if (this.tipoPagamento == TipoPagamento.COMISSAO_AGENCIA) {
            if (this.numeroDocumento == null || this.numeroDocumento.isBlank()) {
                throw new IllegalStateException(
                        "BLOQUEIO: Comissão de agência não pode ser paga sem o número do documento da agência."
                );
            }
            if (this.statusPagamento != StatusPagamento.PRONTO_PARA_PAGAMENTO) {
                throw new IllegalStateException(
                        "Comissão de agência só pode ser paga após o documento ser validado. Status atual: "
                                + this.statusPagamento.getDescricao()
                );
            }
        }

        this.statusPagamento = StatusPagamento.PAGO;
        this.dataPagamento = dataPagamento;
        this.formaPagamento = formaPagamento;
    }

    /**
     * Cancela o pagamento (não pode ser usado se já estiver PAGO).
     */
    public void cancelar() {
        if (this.statusPagamento == StatusPagamento.PAGO) {
            throw new IllegalStateException("Não é possível cancelar um pagamento já efetuado.");
        }
        this.statusPagamento = StatusPagamento.CANCELADO;
        this.ativo = false;
    }
}

