package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sistemas {

    RADIOTV("RadioTv"),
    PETSHOP("Pet Shop"),
    PIZZARIA("Pizzaria"),
    OUTROS("OUTROS");

    private String descricao;
}
