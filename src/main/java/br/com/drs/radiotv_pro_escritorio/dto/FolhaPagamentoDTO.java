package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusFolha;
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
public class FolhaPagamentoDTO {

    private Long folhaPagamentoId;

    // ==========================================
    // VÍNCULOS (IDs para salvar, Nomes para exibição)
    // ==========================================

    // Dados do Funcionário
    private Long funcionarioId;
    private String funcionarioNome;
    private String funcionarioCpf;
    private String funcionarioCargo;
    private Boolean funcionarioVendedor; // Para saber se tem comissão

    // ==========================================
    // REFERÊNCIA TEMPORAL
    // ==========================================

    // Mês de referência da folha (ex: "07/2026")
    private String mesReferencia;

    // Competência (mês trabalhado)
    private String competencia;

    // Data em que a folha foi fechada/gerada
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFechamento;

    // Data em que o salário foi efetivamente pago
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    // ==========================================
    // PROVENTOS (O que o funcionário RECEBE)
    // ==========================================

    // Salário base do funcionário
    private BigDecimal salarioBruto;

    // Total de comissões do vendedor processadas neste mês
    private BigDecimal totalComissoes;

    // Total de proventos = salarioBruto + totalComissoes
    private BigDecimal totalProventos;

    // ==========================================
    // DESCONTOS (O que o funcionário PERDE)
    // ==========================================

    // Total de benefícios (plano de saúde, odontológico, coparticipações)
    private BigDecimal totalBeneficios;

    // Outros descontos (INSS, IR, VT, VR, adiantamentos)
    private BigDecimal totalOutrosDescontos;

    // Total de todos os descontos
    private BigDecimal totalDescontos;

    // ==========================================
    // VALOR FINAL
    // ==========================================

    // Salário líquido = totalProventos - totalDescontos
    private BigDecimal salarioLiquido;

    // ==========================================
    // STATUS E CONTROLE
    // ==========================================

    private StatusFolha statusFolha;

    // ID do pagamento gerado no módulo de Pagamentos (quando paga)
    private Long pagamentoId;

    // Forma de pagamento (TED, PIX, dinheiro, etc.)
    private String formaPagamento;

    private String observacao;
    private Boolean ativa;
}