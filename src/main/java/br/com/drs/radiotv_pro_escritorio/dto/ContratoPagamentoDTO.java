package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
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
public class ContratoPagamentoDTO {

    private Long contratoPagamentoId;

    // Dados do contrato (para exibição na tela)
    private Long contratoId;
    private String clienteNome;
    private String vendedorNome;
    private String agenciaNome; // Pode ser null (agência é opcional)

    // Identificação da parcela
    private Integer numeroParcela;
    private Integer totalParcelas; // Ex: "Parcela 3 de 12"

    // Datas
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamentoReal;

    // Valores
    private BigDecimal valorParcela;
    private BigDecimal valorEfetivoPago;

    // Status (substitui os antigos booleans faturado/paga)
    private StatusRecebimento statusRecebimento;

    // Número da fatura/boleto (preenchido quando FATURADO)
    private String numeroFatura;

    // Controle de comissões (útil para o painel administrativo)
    private Boolean comissaoVendedorLancada;
    private Boolean comissaoAgenciaLancada;

    private Boolean ativo;
}
