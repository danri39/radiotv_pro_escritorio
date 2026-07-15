package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.BeneficiosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Beneficios;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BeneficiosMapper {

    BeneficiosDTO toDTO(Beneficios beneficios);

    Beneficios toEntity(BeneficiosDTO dto);

    void updateEntityFromDto(BeneficiosDTO dto, @MappingTarget Beneficios beneficios);
}
