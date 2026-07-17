package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setores;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sistemas;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(length = 100)
    private String senha;

    @Column(length = 4, unique = true)
    private String chaveUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Papeis papel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_setor", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "setor", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<Setores> setores = new ArrayList<>();

    @Builder.Default
    private Boolean acessoEscritorio = false;

    @Column(length = 40)
    private String chavePrimeiroAcesso;

    @Column(length = 45)
    private String chaveTrocaSenha;

    private Boolean primeiroAcesso;
    private LocalDateTime acessoSistema;

    private Long funcionarioId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    // ==========================================
    // MÉTODOS DE DOMÍNIO (Domain Methods)
    // ==========================================

    public void registrarAcesso() {
        this.acessoSistema = LocalDateTime.now();
    }

    public void finalizarPrimeiroAcesso(String senhaEncriptada) {
        this.senha = senhaEncriptada;
        this.primeiroAcesso = false;
        this.chavePrimeiroAcesso = null;
    }

    public void gerarNovaChaveTrocaSenha() {
        this.chaveTrocaSenha = UUID.randomUUID().toString().substring(0, 40);
    }

    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
        this.chaveTrocaSenha = null;
    }
}