package br.com.drs.radiotv_pro_escritorio.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "conta_bancaria")
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
