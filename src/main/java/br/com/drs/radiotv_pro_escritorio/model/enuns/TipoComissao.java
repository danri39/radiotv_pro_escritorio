package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoComissao {
    VENDEDOR("Vendedor"),
    AGENCIA("Agencia");

    private String descricao;
}