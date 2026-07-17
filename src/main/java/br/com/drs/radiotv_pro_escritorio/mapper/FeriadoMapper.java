package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FeriadoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Feriado;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeriadoMapper {

    FeriadoDTO toDTO(Feriado feriado);

    Feriado toEntity(FeriadoDTO dto);

    void updateEntityFromDto(FeriadoDTO dto, @MappingTarget Feriado feriado);
}
