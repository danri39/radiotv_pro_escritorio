package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends GenericRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByChaveUsuario(String chaveUsuario);

    Optional<Usuario>  findByChaveUsuario(String chaveUsuario);

    Optional<Usuario> findByChavePrimeiroAcesso(String chave);

    Optional<Usuario> findByChaveTrocaSenha(String chave);

    boolean existsByEmail(String email);
}
