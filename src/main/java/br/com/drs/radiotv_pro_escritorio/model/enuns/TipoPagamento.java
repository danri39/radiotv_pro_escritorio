package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPagamento {

    COMPRA("Compra"),
    MANUAL("Manual");

    private String descricao;
}
