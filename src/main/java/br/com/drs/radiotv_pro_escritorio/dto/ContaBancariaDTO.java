package br.com.drs.radiotv_pro_escritorio.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaBancariaDTO {

    private Long contaBancariaId;

    private String nomeBanco;

    private String codigoBanco;

    private String agencia;

    private String digitoAgencia;

    private String contaCorrente;

    private String digitoConta;

    private String codigoCedente;

    private String carteira;
}
