package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setores;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sistemas;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioId;

    @Column(length = 50)
    private String primeiroNome;

    @Column(length = 150, unique = true)
    private String email;

    private String senha;

    @Column(length = 4, unique = true)
    private String chaveUsuario;

    @Builder.Default
    private Boolean acessoEscritorio = false;

    @Enumerated(EnumType.STRING)
    private Papeis papeis;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_setor", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "setores", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private List<Setores> setores;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_sistemas", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "sistemas", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private List<Sistemas> sistemas;

    @Column(length = 40, unique = true)
    private String chavePrimeiroAcesso;

    @Column(length = 45, unique = true)
    private String chaveTrocaSenha;

    private LocalDateTime acessoSistema;

    private Long funcionarioId;

    @Builder.Default
    private Boolean primeiroAcesso = true;

    @Builder.Default
    private Boolean ativo = true;

    public void finalizarPrimeiroAcesso(String senhaEncriptada) {
        this.senha = senhaEncriptada;
        this.primeiroAcesso = false;
        this.chavePrimeiroAcesso = null;
    }

    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
        this.chaveTrocaSenha = null;
    }
}
