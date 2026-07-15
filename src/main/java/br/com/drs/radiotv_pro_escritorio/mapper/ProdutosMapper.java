package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ProdutosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Produtos;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutosMapper {

    ProdutosDTO toDTO(Produtos produtos);

    Produtos toEntity(ProdutosDTO dto);

    void updateEntityFromDto(ProdutosDTO dto, @MappingTarget Produtos produtos);
}
