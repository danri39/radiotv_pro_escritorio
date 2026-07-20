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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilhosService {

    private final FilhosRepository repository;
    private final FilhosMapper mapper;
    private final FuncionarioRepository funcionarioRepository;  // ← Adicione isso

    @Transactional
    public FilhosDTO salvar(FilhosDTO dto) {
        // 1. Validação do CPF
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        // 2. Validação do funcionarioId
        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }

        // 3. Busca o funcionário
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário com ID " + dto.getFuncionarioId() + " não encontrado."));

        // 4. Cria a entidade manualmente (não usa o mapper)
        Filhos entity = new Filhos();
        entity.setFuncionario(funcionario);
        entity.setNome(dto.getNome());
        entity.setCpf(dto.getCpf());
        entity.setRg(dto.getRg());
        entity.setTelefone(dto.getTelefone());
        entity.setCelular(dto.getCelular());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setSexo(dto.getSexo());
        entity.setFormacao(dto.getFormacao());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

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
        // 1. Validações
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }

        // 2. Busca a entidade existente
        Filhos entityExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Filho com ID " + id + " não encontrado para atualização."));

        // 3. Busca o funcionário
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário com ID " + dto.getFuncionarioId() + " não encontrado."));

        // 4. Atualiza manualmente
        entityExistente.setFuncionario(funcionario);
        entityExistente.setNome(dto.getNome());
        entityExistente.setCpf(dto.getCpf());
        entityExistente.setRg(dto.getRg());
        entityExistente.setTelefone(dto.getTelefone());
        entityExistente.setCelular(dto.getCelular());
        entityExistente.setDataNascimento(dto.getDataNascimento());
        entityExistente.setSexo(dto.getSexo());
        entityExistente.setFormacao(dto.getFormacao());
        entityExistente.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

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