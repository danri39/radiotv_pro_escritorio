package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDTO(Usuario entidade);

    Usuario paraEntity(UsuarioDTO dto);

    @Mapping(target = "usuarioId", ignore = true)
    @Mapping(target = "chaveUsuario", ignore = true)  // ← IGNORA NO UPDATE
    @Mapping(target = "senha", ignore = true)          // ← IGNORA NO UPDATE
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "primeiroAcesso", ignore = true)
    @Mapping(target = "chavePrimeiroAcesso", ignore = true)
    @Mapping(target = "chaveTrocaSenha", ignore = true)
    void updateEntityFromDto(UsuarioDTO dto, @MappingTarget Usuario entidade);
}