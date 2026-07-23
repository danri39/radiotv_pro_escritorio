package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Papeis {

    SUPERUSUARIO("Super Usuário"),
    ADMINISTRADOR("Administrador"),
    GERENTE("Gerente"),
    USUARIO("Usuário"),
    ESCRITORIO("Escritório"),
    RH("RH"),
    FINANCEIRO("Financeiro"),
    RADIO("Rádio"),
    TELEVISAO("Televisão"),
    CONVIDADO("Convidado"),
    CLIENTE("Cliente"),
    AGENCIA("Agencia");

    private String descricao;
}
