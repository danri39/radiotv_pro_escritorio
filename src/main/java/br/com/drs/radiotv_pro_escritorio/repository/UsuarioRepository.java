package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByChaveUsuario(String chaveUsuario);

    Optional<Usuario>  findByChaveUsuario(String chaveUsuario);

    Optional<Usuario> findByChavePrimeiroAcesso(String chave);

    Optional<Usuario> findByChaveTrocaSenha(String chave);

    boolean existsByEmail(String email);
}
