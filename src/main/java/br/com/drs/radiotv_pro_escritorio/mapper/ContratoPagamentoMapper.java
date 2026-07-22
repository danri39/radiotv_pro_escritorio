package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContratoPagamentoMapper {

    @Mapping(target = "contratoId", source = "contrato.contratoId")
    @Mapping(target = "clienteNome", source = "contrato.cliente.numero")
    @Mapping(target = "vendedorNome", source = "contrato.vendedor.funcionario.nome")
    @Mapping(target = "agenciaNome", source = "contrato.agencia.nomeFantasia")
    @Mapping(target = "totalParcelas", source = "contrato.quantidadeParcelas")
    ContratoPagamentoDTO toDTO(ContratoPagamento entity);

    @Mapping(target = "contrato", ignore = true)
    ContratoPagamento toEntity(ContratoPagamentoDTO dto);

    List<ContratoPagamentoDTO> toDTOList(List<ContratoPagamento> entities);
}