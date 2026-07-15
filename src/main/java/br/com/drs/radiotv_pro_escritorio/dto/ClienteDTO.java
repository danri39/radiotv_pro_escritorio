package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDTO {

    private Long clienteId;

    private String razaoSocial;

    private String nomeFantasia;

    private TipoPessoa tipoPessoa;

    private String cpf;

    private String cnpj;

    private String rg;

    private String inscricao;

    private String email;

    private String telefone;

    private String celular;

    private String pessoaContato;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInauguracao;

    private Boolean contratosValidos;

    private Long ramoAtividade;

    @Builder.Default
    private Boolean ativo = true;
}
