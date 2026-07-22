package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficiario;
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
public class LancamentoBeneficioMensalDTO {

    private Long lancamentoId;

    // ==========================================
    // VÍNCULOS (IDs para salvar, Nomes para exibição)
    // ==========================================

    // Dados do Funcionário
    private Long funcionarioId;
    private String funcionarioNome;
    private String funcionarioCpf;

    // Dados do Plano de Benefício
    private Long planoBeneficioId;
    private String planoBeneficioNome; // Ex: "Plano de Saúde Unimed - Titular"
    private String operadora; // Ex: "Unimed"
    private BigDecimal valorMensalFixo; // Valor fixo atual do plano (para exibição)

    // ==========================================
    // IDENTIFICAÇÃO DO BENEFICIÁRIO
    // ==========================================

    private TipoBeneficiario tipoBeneficiario; // TITULAR, FILHO, CONJUGE, etc.
    private String nomeDependente; // Ex: "João Silva Filho" (se aplicável)
    private String cpfDependente;

    // ==========================================
    // REFERÊNCIA TEMPORAL
    // ==========================================

    // Mês de referência (ex: "07/2026")
    private String mesReferencia;

    // Data em que o lançamento foi registrado
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataLancamento;

    // ==========================================
    // VALORES
    // ==========================================

    // Valor da coparticipação do mês (consultas, exames, etc.)
    private BigDecimal valorCoparticipacao;

    // Quantidade de utilizações no mês
    private Integer quantidadeUtilizacoes;

    // Descrição detalhada das utilizações
    private String descricaoUtilizacoes;

    // ==========================================
    // VALOR TOTAL CALCULADO (somente leitura)
    // ==========================================

    // Valor total do mês = valorFixo + coparticipacao
    // Calculado automaticamente pelo sistema
    private BigDecimal valorTotalMensal;

    // ==========================================
    // CONTROLE
    // ==========================================

    private String observacao;
    private Boolean ativo;
}