package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FamiliaDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.FamiliaMapper;
import br.com.drs.radiotv_pro_escritorio.model.Familia;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.repository.FamiliaRepository;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FamiliaService {

    private final FamiliaRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final FamiliaMapper mapper;

    @Transactional
    public FamiliaDTO salvar(FamiliaDTO dto) {

        if (!DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        Familia entity = mapper.toEntity(dto);

        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado."));

        entity.setFuncionario(funcionario);

        Familia saved = repository.save(entity);

        return mapper.toDTO(saved);
    }

    public List<Familia> listarTodos() {
        return repository.findAll();
    }

    public Optional<Familia> BuscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public FamiliaDTO atualizar(Long id, FamiliaDTO dto) {
        if(!DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }
        Familia entity = mapper.toEntity(dto);
        Familia saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
