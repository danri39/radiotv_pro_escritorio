package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ItemCompraDTO;
import br.com.drs.radiotv_pro_escritorio.model.ItemCompra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemCompraMapper {

    ItemCompraMapper INSTANCE = Mappers.getMapper(ItemCompraMapper.class);

    @Mapping(target = "compra", ignore = true) // O relacionamento é gerenciado pelo ComprasService
    ItemCompra toEntity(ItemCompraDTO dto);

    ItemCompraDTO toDTO(ItemCompra entity);

    List<ItemCompraDTO> toDTOList(List<ItemCompra> entities);

    List<ItemCompra> toEntityList(List<ItemCompraDTO> dtos);
}