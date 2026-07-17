package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.FilhosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import br.com.drs.radiotv_pro_escritorio.repository.FilhosRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilhosService {

    private final FilhosRepository repository;
    private final FilhosMapper mapper;

    @Transactional
    public FilhosDTO salvar(FilhosDTO dto) {
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }
        Filhos entity = mapper.toEntity(dto);
        Filhos saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    public List<FilhosDTO> listarTodos() {
        return repository.findAllWithFuncionario().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public FilhosDTO buscarPorId(Long id) {
        Filhos entity = repository.findByIdWithFuncionario(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Filho não encontrado"));
        return mapper.toDTO(entity);
    }

    @Transactional
    public FilhosDTO atualizar(Long id, FilhosDTO dto) {
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }
        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }
        Filhos entityExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Filho com ID " + id + " não encontrado para atualização."));
        mapper.updateEntityFromDto(dto, entityExistente);
        Filhos saved = repository.save(entityExistente);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Filho com ID " + id + " não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }
}