package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.LancamentoBeneficioMensalDTO;
import br.com.drs.radiotv_pro_escritorio.model.LancamentoBeneficioMensal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LancamentoBeneficioMensalMapper {

    @Mapping(target = "funcionarioId", source = "funcionario.id") // CORREÇÃO: 'id' em vez de 'funcionarioId'
    @Mapping(target = "funcionarioNome", source = "funcionario.nome")
    @Mapping(target = "funcionarioCpf", source = "funcionario.cpf")

    @Mapping(target = "planoBeneficioId", source = "planoBeneficio.planoBeneficioId")
    @Mapping(target = "planoBeneficioNome", source = "planoBeneficio.nome")
    @Mapping(target = "operadora", source = "planoBeneficio.operadora")
    @Mapping(target = "valorMensalFixo", source = "planoBeneficio.valorMensalFixo")

    @Mapping(target = "valorTotalMensal", source = ".", qualifiedByName = "calcularTotalMensal")
    LancamentoBeneficioMensalDTO toDTO(LancamentoBeneficioMensal entity);

    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "planoBeneficio", ignore = true)
    LancamentoBeneficioMensal toEntity(LancamentoBeneficioMensalDTO dto);

    List<LancamentoBeneficioMensalDTO> toDTOList(List<LancamentoBeneficioMensal> entities);

    @Named("calcularTotalMensal")
    default java.math.BigDecimal calcularTotalMensal(LancamentoBeneficioMensal entity) {
        return entity != null ? entity.calcularTotalMensal() : java.math.BigDecimal.ZERO;
    }
}