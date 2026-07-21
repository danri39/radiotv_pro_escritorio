package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusRecebimento {

    A_FATURAR("A Faturar"),
    FATURADO("Faturado (Boleto/Nota Emitida)"),
    RECEBIDO("Recebido do Cliente"),
    ATRASADO("Atrasado"),
    CANCELADO("Cancelado");

    private final String descricao;
}