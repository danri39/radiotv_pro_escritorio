package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FolhaPagamentoMapper {

    FolhaPagamentoDTO toDTO(FolhaPagamento pagamento);

    FolhaPagamento toEntity(FolhaPagamentoDTO dto);

    void updateEntityFromDto(FolhaPagamentoDTO dto, @MappingTarget FolhaPagamento pagamento);
}
