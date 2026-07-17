package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.PontoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Ponto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.parameters.P;

@Mapper(componentModel = "spring")
public interface PontoMapper {

    PontoDTO toDTO(Ponto ponto);

    Ponto toEntity(PontoDTO dto);

    void updateEntityFromDto(PontoDTO dto, @MappingTarget Ponto ponto);
}
