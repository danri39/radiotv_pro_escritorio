package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FamiliaDTO;
import br.com.drs.radiotv_pro_escritorio.model.Familia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FamiliaMapper {

    FamiliaDTO toDTO(Familia familia);

    @Mapping(target = "funcionario", ignore = true)
    Familia toEntity(FamiliaDTO dto);

    @Mapping(target = "funcionario", ignore = true)
    void updateEntityFromDto(FamiliaDTO dto, @MappingTarget Familia familia);
}
