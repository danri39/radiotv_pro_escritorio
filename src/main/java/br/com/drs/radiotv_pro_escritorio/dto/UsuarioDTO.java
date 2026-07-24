package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private Long usuarioId;  // ← DEVE EXISTIR ESTE CAMPO

    private String nome;

    private String email;

    private String senha;

    private String chaveUsuario;

    private Papeis papel;

    private List<Setor> setor;

    private Boolean acessoEscritorio;

    private String chavePrimeiroAcesso;

    private String chaveTrocaSenha;

    private Boolean primeiroAcesso;

    private LocalDateTime acessoSistema;

    private Long funcionarioId;

    private Boolean ativo;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}