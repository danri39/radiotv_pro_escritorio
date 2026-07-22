package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficiario;
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
@Table(name = "lancamento_beneficio_mensal")
public class LancamentoBeneficioMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lancamentoId;

    // ==========================================
    // VÍNCULOS
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plano_beneficio_id", nullable = false)
    private PlanoBeneficio planoBeneficio;

    // ==========================================
    // IDENTIFICAÇÃO DO BENEFICIÁRIO
    // ==========================================

    // Tipo de beneficiário (TITULAR, FILHO, CONJUGE, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBeneficiario tipoBeneficiario;

    // Nome do dependente (preenchido apenas se tipoBeneficiario != TITULAR)
    // Ex: "João Silva Filho"
    private String nomeDependente;

    // CPF do dependente (opcional, para identificação)
    private String cpfDependente;

    // ==========================================
    // REFERÊNCIA TEMPORAL
    // ==========================================

    // Mês de referência do lançamento (formato "MM/yyyy", ex: "07/2026")
    @Column(nullable = false, length = 7)
    private String mesReferencia;

    // Data em que o lançamento foi registrado
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate dataLancamento;

    // ==========================================
    // VALOR VARIÁVEL DO MÊS
    // ==========================================

    // Valor da coparticipação do mês (consultas, exames, etc.)
    // Se não houver coparticipação, pode ser null ou zero
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorCoparticipacao = BigDecimal.ZERO;

    // Quantidade de utilizações no mês (ex: 3 consultas)
    private Integer quantidadeUtilizacoes;

    // Descrição detalhada das utilizações (ex: "2 consultas clínicas + 1 exame de sangue")
    @Column(length = 500)
    private String descricaoUtilizacoes;

    // ==========================================
    // CONTROLE
    // ==========================================

    @Column(length = 500)
    private String observacao;

    @Builder.Default
    private Boolean ativo = true;

    // ==========================================
    // MÉTODO HELPER: Calcula o total do mês
    // ==========================================

    /**
     * Calcula o valor total a ser descontado do funcionário neste mês.
     * Fórmula: valorFixo (do plano) + valorCoparticipacao (variável do mês)
     *
     * @return O valor total do benefício para este mês
     */
    public BigDecimal calcularTotalMensal() {
        BigDecimal valorFixo = planoBeneficio != null && planoBeneficio.getValorMensalFixo() != null
                ? planoBeneficio.getValorMensalFixo()
                : BigDecimal.ZERO;

        BigDecimal coparticipacao = valorCoparticipacao != null
                ? valorCoparticipacao
                : BigDecimal.ZERO;

        return valorFixo.add(coparticipacao);
    }

    /**
     * Retorna apenas o valor da coparticipação (para relatórios específicos).
     */
    public BigDecimal getValorCoparticipacaoOuZero() {
        return valorCoparticipacao != null ? valorCoparticipacao : BigDecimal.ZERO;
    }
}