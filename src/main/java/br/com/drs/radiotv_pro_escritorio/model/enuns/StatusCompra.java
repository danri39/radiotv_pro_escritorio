package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCompra {

    PENDENTE("Pendente de Aprovação"),
    APROVADA("Aprovada"),
    RECUSADA("Recusada"),
    PAGA("Paga");

    private final String descricao;
}