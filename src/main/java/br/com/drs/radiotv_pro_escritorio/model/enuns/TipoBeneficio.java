package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoBeneficio {

    PLANO_SAUDE("Plano de Saúde"),
    PLANO_ODONTOLOGICO("Plano Odontológico"),
    SEGURO_VIDA("Seguro de Vida"),
    VALE_ALIMENTACAO("Vale-Alimentação"),
    VALE_REFEICAO("Vale-Refeição"),
    VALE_TRANSPORTE("Vale-Transporte"),
    AUXILIO_CRECHE("Auxílio-Creche"),
    OUTROS("Outros");

    private final String descricao;
}