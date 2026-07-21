package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusPagamento {

    PENDENTE("Pendente de Lançamento"),
    AGUARDANDO_DOCUMENTO("Aguardando Documento da Agência"),
    PRONTO_PARA_PAGAMENTO("Pronto para Pagamento (Documento Validado)"),
    PAGO("Pago"),
    CANCELADO("Cancelado");

    private final String descricao;
}