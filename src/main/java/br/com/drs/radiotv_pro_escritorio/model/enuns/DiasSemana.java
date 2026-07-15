package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiasSemana {

    DOMINGO(1, "Domingo"),
    SEGUNDA(2, "Segunda"),
    TERCA(3, "Terça"),
    QUARTA(4, "Quarta"),
    QUINTA(5, "Quinta"),
    SEXTA(6, "Sexta"),
    SABADO(7, "Sábado");

    private int numero;

    private String descricao;
}
