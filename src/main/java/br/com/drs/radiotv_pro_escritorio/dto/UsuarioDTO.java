package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setores;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sistemas;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String chaveUsuario;

    private Papeis papel;

    private List<Setores> setores = new ArrayList<>();

    @Builder.Default
    private Boolean acessoEscritorio = false;

    private String chavePrimeiroAcesso;

    private String chaveTrocaSenha;

    private Boolean primeiroAcesso;

    private LocalDateTime acessoSistema;

    private Long funcionarioId;

    private Boolean ativo = true;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}
