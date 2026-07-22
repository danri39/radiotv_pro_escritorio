package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FuncionarioDTO;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.FuncionarioMapper;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;

    @Transactional
    public FuncionarioDTO salvar(FuncionarioDTO dto) {
        log.info("Iniciando salvamento de funcionário: {}", dto.getNome());

        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegraNegocioException("E-mail já cadastrado no sistema.");
        }

        try {
            Funcionario entity = mapper.toEntity(dto);
            Funcionario saved = repository.save(entity);
            return mapper.toDTO(saved);
        } catch (Exception e) {
            log.error("Erro ao salvar no banco de dados: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao persistir dados: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Optional<FuncionarioDTO> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    @Transactional
    public FuncionarioDTO atualizar(FuncionarioDTO dto, Long id) {
        Funcionario existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID não encontrado: " + id));
        mapper.updateEntityFromDto(dto, existing);
        return mapper.toDTO(repository.save(existing));
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }

    public List<FuncionarioDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream().map(mapper::toDTO).toList();
    }
}