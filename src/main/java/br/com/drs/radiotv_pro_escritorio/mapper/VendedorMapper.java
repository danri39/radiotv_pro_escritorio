package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.VendedorDTO;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VendedorMapper {

    VendedorDTO toDTO(Vendedor vendedor);

    Vendedor toEntity(VendedorDTO dto);

    void updateEntityFromDto(VendedorDTO dto, @MappingTarget Vendedor vendedor);
}
