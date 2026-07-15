package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ComprasDTO;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComprasMapper {

    ComprasDTO toDTO(Compras compras);

    Compras toEntity(ComprasDTO dto);

    void updateEntityFromDto(ComprasDTO dto, @MappingTarget Compras compras);
}
