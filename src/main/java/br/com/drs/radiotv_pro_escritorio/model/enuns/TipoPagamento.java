package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPagamento {

    COMISSAO_AGENCIA("Comissão de Agência"),
    COMPRA_APROVADA("Compra Aprovada"),
    CONTA_DIVERSSA("Conta Diversa (Luz, Água, Internet, etc.)"),
    SALARIO("Salário de Funcionário"),
    VALE_TRANSPORTE("Vale-Transporte"),
    VALE_REFEICAO("Vale-Refeição"),
    COMISSAO_VENDEDOR("Comissão de Vendedor (quando paga separadamente)"),
    OUTROS("Outros");

    private final String descricao;
}