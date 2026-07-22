package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentosDTO {

    private Long pagamentoId;

    // ==========================================
    // CLASSIFICAÇÃO
    // ==========================================
    private TipoPagamento tipoPagamento;
    private StatusPagamento statusPagamento;
    private String descricao;
    private String beneficiario;

    // ==========================================
    // VALORES E DATAS
    // ==========================================
    private BigDecimal valor;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private String formaPagamento;

    // ==========================================
    // DOCUMENTO DA AGÊNCIA
    // ==========================================
    private String numeroDocumento;
    private String caminhoArquivoDocumento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataUploadDocumento;

    // ==========================================
    // VÍNCULOS (IDs para salvar, Nomes para exibição)
    // ==========================================

    // Vínculo com Agência (para COMISSAO_AGENCIA)
    private Long agenciaId;
    private String agenciaNome;

    // Vínculo com Parcela do Contrato (para COMISSAO_AGENCIA)
    private Long contratoPagamentoId;
    private Integer numeroParcela; // Para exibir "Parcela 3 de 12"

    // Vínculo com Compra (para COMPRA_APROVADA)
    private Long compraId;

    // Vínculo com Funcionário (para SALARIO, VALE, COMISSAO_VENDEDOR)
    private Long funcionarioId;
    private String funcionarioNome;

    // ==========================================
    // CONTROLE
    // ==========================================
    private String observacao;
    private Boolean ativo;
}
