package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusFolha {

    ABERTA("Em Aberto - Pode ser Editada"),
    FECHADA("Fechada - Aguardando Pagamento"),
    PAGA("Paga ao Funcionário"),
    CANCELADA("Cancelada");

    private final String descricao;
}