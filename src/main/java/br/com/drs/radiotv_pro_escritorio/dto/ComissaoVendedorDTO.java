package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusComissao;
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
public class ComissaoVendedorDTO {

    private Long comissaoVendedorId;

    // ==========================================
    // VÍNCULOS (IDs para salvar, Nomes para exibição)
    // ==========================================

    // Dados do Vendedor
    private Long vendedorId;
    private String vendedorNome; // Nome do funcionário vinculado ao vendedor

    // Dados da Parcela do Contrato (origem da comissão)
    private Long contratoPagamentoId;
    private Integer numeroParcela; // Ex: 3 (para exibir "Parcela 3 de 12")
    private BigDecimal valorParcela; // Valor original da parcela
    private BigDecimal valorEfetivoPago; // Valor que o cliente realmente pagou

    // Dados do Contrato (para contexto)
    private Long contratoId;
    private String clienteNome; // Nome do cliente do contrato
    private Integer totalParcelas; // Ex: 12 (para exibir "Parcela 3 de 12")

    // ==========================================
    // DADOS DA COMISSÃO
    // ==========================================

    // Mês de referência (ex: "07/2026")
    private String mesReferencia;

    // Valor da comissão calculada
    private BigDecimal valorComissao;

    // Percentual aplicado (ex: 10 para 10%)
    private Integer percentualAplicado;

    // Data em que a comissão foi calculada (quando o cliente pagou)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCalculo;

    // ==========================================
    // CONTROLE DE PROCESSAMENTO
    // ==========================================

    private StatusComissao statusComissao;

    // Data em que foi processada na folha (null enquanto PENDENTE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataProcessamento;

    // ID da folha onde foi processada (null enquanto PENDENTE)
    private Long folhaPagamentoId;

    private Boolean ativa;
}