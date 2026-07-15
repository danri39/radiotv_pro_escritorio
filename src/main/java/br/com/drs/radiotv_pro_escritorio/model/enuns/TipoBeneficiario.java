package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoBeneficiario {
    TITULAR,
    CONJUGE,
    FILHO,
    OUTRO_DEPENDENTE
}