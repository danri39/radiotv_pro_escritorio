package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ComissaoVendedorDTO;
import br.com.drs.radiotv_pro_escritorio.model.ComissaoVendedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComissaoVendedorMapper {

    /**
     * Converte Entidade para DTO
     * Mapeia os dados dos relacionamentos aninhados
     */
    @Mapping(target = "vendedorId", source = "vendedor.vendedorId")
    @Mapping(target = "vendedorNome", source = "vendedor.funcionario.nome")

    @Mapping(target = "contratoPagamentoId", source = "contratoPagamento.contratoPagamentoId")
    @Mapping(target = "numeroParcela", source = "contratoPagamento.numeroParcela")
    @Mapping(target = "valorParcela", source = "contratoPagamento.valorParcela")
    @Mapping(target = "valorEfetivoPago", source = "contratoPagamento.valorEfetivoPago")

    @Mapping(target = "contratoId", source = "contratoPagamento.contrato.contratoId")
    @Mapping(target = "clienteNome", source = "contratoPagamento.contrato.cliente.numero") // CORREÇÃO: usando 'numero' em vez de 'nome'
    @Mapping(target = "totalParcelas", source = "contratoPagamento.contrato.quantidadeParcelas")
    ComissaoVendedorDTO toDTO(ComissaoVendedor entity);

    /**
     * Converte DTO para Entidade
     */
    @Mapping(target = "vendedor", ignore = true)
    @Mapping(target = "contratoPagamento", ignore = true)
    ComissaoVendedor toEntity(ComissaoVendedorDTO dto);

    /**
     * Converte Lista de Entidades para Lista de DTOs
     */
    List<ComissaoVendedorDTO> toDTOList(List<ComissaoVendedor> entities);
}