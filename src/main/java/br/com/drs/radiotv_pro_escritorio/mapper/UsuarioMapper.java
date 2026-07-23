package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UsuarioMapper {

    // MapStruct vai mapear automaticamente todos os campos com o mesmo nome
    UsuarioDTO toDTO(Usuario usuario);

    // MapStruct vai mapear automaticamente todos os campos com o mesmo nome
    Usuario toEntity(UsuarioDTO dto);

    // Para atualização, ignorar campos que não podem ser alterados
    @Mapping(target = "usuarioId", ignore = true)
    @Mapping(target = "chaveUsuario", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "primeiroAcesso", ignore = true)
    @Mapping(target = "chavePrimeiroAcesso", ignore = true)
    @Mapping(target = "chaveTrocaSenha", ignore = true)
    @Mapping(target = "acessoSistema", ignore = true)
    void updateEntityFromDto(UsuarioDTO dto, @MappingTarget Usuario entidade);
}