package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDTO(Usuario usuario);

    Usuario toEntity(UsuarioDTO dto);

    @Mapping(target = "usuarioId", ignore = true)
    @Mapping(target = "chaveUsuario", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "chavePrimeiroAcesso", ignore = true)
    @Mapping(target = "chaveTrocaSenha", ignore = true)
    void updateEntityFromDto(UsuarioDTO dto, @MappingTarget Usuario entidade);
}
