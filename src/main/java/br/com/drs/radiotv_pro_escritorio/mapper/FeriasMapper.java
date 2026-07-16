package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FeriasDTO;
import br.com.drs.radiotv_pro_escritorio.model.Ferias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeriasMapper {

    FeriasDTO toDTO(Ferias ferias);

    Ferias toEntity(FeriasDTO dto);

    void updateEntityFromDto(FeriasDTO dto, @MappingTarget Ferias ferias);
}
