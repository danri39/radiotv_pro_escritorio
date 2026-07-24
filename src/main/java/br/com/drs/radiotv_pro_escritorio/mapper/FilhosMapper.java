package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FilhosMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "funcionario.id", target = "funcionarioId")
    @Mapping(source = "funcionario.nome", target = "nomeFuncionario")
    @Mapping(source = "nome", target = "nome")
    @Mapping(source = "cpf", target = "cpf")
    @Mapping(source = "rg", target = "rg")
    @Mapping(source = "telefone", target = "telefone")
    @Mapping(source = "celular", target = "celular")
    @Mapping(source = "dataNascimento", target = "dataNascimento")
    @Mapping(source = "sexo", target = "sexo")
    @Mapping(source = "formacao", target = "formacao")
    @Mapping(source = "ativo", target = "ativo")
    FilhosDTO toDTO(Filhos filhos);

    @Mapping(target = "funcionario", ignore = true)
    Filhos toEntity(FilhosDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "funcionario", ignore = true)
    void updateEntityFromDto(FilhosDTO dto, @MappingTarget Filhos filhos);
}