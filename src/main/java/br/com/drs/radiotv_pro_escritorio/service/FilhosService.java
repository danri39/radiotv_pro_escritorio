package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.FilhosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.repository.FilhosRepository;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilhosService {

    private final FilhosRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final FilhosMapper mapper;

    @Transactional
    public FilhosDTO salvar(FilhosDTO dto) {
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }

        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário com ID " + dto.getFuncionarioId() + " não encontrado."));

        Filhos entity = mapper.toEntity(dto);
        entity.setFuncionario(funcionario);

        Filhos saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    public List<Filhos> listarTodos() {
        return repository.findAll();
    }

    public Optional<Filhos> buscarPorId(Long id) {
        return repository.findById(id);
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

        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário com ID " + dto.getFuncionarioId() + " não encontrado."));

        // Atualiza os campos
        mapper.updateEntityFromDto(dto, entityExistente);
        entityExistente.setFuncionario(funcionario);

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