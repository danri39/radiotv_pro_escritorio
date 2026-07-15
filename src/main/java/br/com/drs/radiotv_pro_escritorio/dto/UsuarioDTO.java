package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setores;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sistemas;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private Long usuarioId;

    private String primeiroNome;

    private String email;

    private String senha;

    private String chaveUsuario;

    @Builder.Default
    private Boolean acessoEscritorio = false;

    private Papeis papeis;

    private List<Setores> setores;

    private List<Sistemas> sistemas;

    private String chavePrimeiroAcesso;

    private String chaveTrocaSenha;

    private LocalDateTime acessoSistema;

    private Long funcionarioId;

    @Builder.Default
    private Boolean primeiroAcesso = true;

    @Builder.Default
    private Boolean ativo = true;
}
