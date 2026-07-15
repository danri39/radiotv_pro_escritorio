package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.FilhosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.repository.FilhosRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilhosService {

    private final FilhosRepository repository;
    private final FilhosMapper mapper;

    @Transactional
    public FilhosDTO salvar(FilhosDTO dto) {
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (!DocumentoUtils.isCPF(dto.getCpf())) {
                throw new RegraNegocioException("CPF inválido!");
            }
        }
        Filhos entity = mapper.toEntity(dto);

        Funcionario funcionario = repository.findById(dto.getFuncionario().getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado.")).getFuncionario();

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
        if(!DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }
        Filhos entity = mapper.toEntity(dto);
        Filhos saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
