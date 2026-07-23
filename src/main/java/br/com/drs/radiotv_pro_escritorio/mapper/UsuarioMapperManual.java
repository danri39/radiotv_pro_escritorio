package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapperManual {

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioDTO.builder()
                .usuarioId(usuario.getUsuarioId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .chaveUsuario(usuario.getChaveUsuario())
                .papel(usuario.getPapel())
                .setor(usuario.getSetor())
                .acessoEscritorio(usuario.getAcessoEscritorio())
                .chavePrimeiroAcesso(usuario.getChavePrimeiroAcesso())
                .chaveTrocaSenha(usuario.getChaveTrocaSenha())
                .primeiroAcesso(usuario.getPrimeiroAcesso())
                .acessoSistema(usuario.getAcessoSistema())
                .funcionarioId(usuario.getFuncionarioId())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        return Usuario.builder()
                .usuarioId(dto.getUsuarioId())
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .chaveUsuario(dto.getChaveUsuario())
                .papel(dto.getPapel())
                .setor(dto.getSetor())
                .acessoEscritorio(dto.getAcessoEscritorio())
                .chavePrimeiroAcesso(dto.getChavePrimeiroAcesso())
                .chaveTrocaSenha(dto.getChaveTrocaSenha())
                .primeiroAcesso(dto.getPrimeiroAcesso())
                .acessoSistema(dto.getAcessoSistema())
                .funcionarioId(dto.getFuncionarioId())
                .ativo(dto.getAtivo())
                .criadoEm(dto.getCriadoEm())
                .atualizadoEm(dto.getAtualizadoEm())
                .build();
    }

    public void updateEntityFromDto(UsuarioDTO dto, Usuario entidade) {
        if (dto == null || entidade == null) {
            return;
        }

        // NÃO atualizar usuarioId, chaveUsuario, senha, criadoEm, acessoSistema
        entidade.setNome(dto.getNome());
        entidade.setEmail(dto.getEmail());
        entidade.setPapel(dto.getPapel());
        entidade.setSetor(dto.getSetor());
        entidade.setAcessoEscritorio(dto.getAcessoEscritorio());
        entidade.setFuncionarioId(dto.getFuncionarioId());
        entidade.setAtivo(dto.getAtivo());
        // atualizadoEm é gerenciado pelo Spring Data JPA com @LastModifiedDate
    }
}