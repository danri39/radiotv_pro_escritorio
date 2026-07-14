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
    CONVIDADO("Convidado"),
    CLIENTE("Cliente"),
    AGENCIA("Agencia");

    private String descricao;
}
