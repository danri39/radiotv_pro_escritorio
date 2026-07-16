package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ConfiguracaoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Configuracao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConfiguracaoMapper {

    ConfiguracaoDTO toDTO(Configuracao configuracao);

    Configuracao toEntity(ConfiguracaoDTO dto);

    void updateEntityFromDto(ConfiguracaoDTO dto, @MappingTarget Configuracao configuracao);
}
