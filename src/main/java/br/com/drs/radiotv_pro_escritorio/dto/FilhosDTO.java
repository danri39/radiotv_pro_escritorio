package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Formacao;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sexo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilhosDTO {

    private Long id;

    private Long funcionarioId;

    private String nomeFuncionario;

    private String nome;

    private String cpf;

    private String rg;

    private String telefone;

    private String celular;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    private Sexo sexo;

    private Formacao formacao;

    @Builder.Default
    private Boolean ativo = true;
}