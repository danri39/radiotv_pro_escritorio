package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FuncionarioDTO;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {

    FuncionarioDTO toDTO(Funcionario entity);

    Funcionario toEntity(FuncionarioDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(FuncionarioDTO dto, @MappingTarget Funcionario entity);
}
