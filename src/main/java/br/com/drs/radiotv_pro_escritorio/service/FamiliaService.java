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
        // 1. Validação de segurança contra null/NaN vindo do frontend
        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }

        if (dto.getCpf() != null && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        // 2. Busca o funcionário (se o ID for inválido, cai no orElseThrow com mensagem clara)
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário com ID " + dto.getFuncionarioId() + " não encontrado."));

        // 3. Mapeia e associa
        Familia entity = mapper.toEntity(dto);
        entity.setFuncionario(funcionario);

        Familia saved = repository.save(entity);

        return mapper.toDTO(saved);
    }

    public List<Familia> listarTodos() {
        return repository.findAll();
    }

    public Optional<Familia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public FamiliaDTO atualizar(Long id, FamiliaDTO dto) {
        // 1. Validação de segurança
        if (dto.getFuncionarioId() == null) {
            throw new RegraNegocioException("O vínculo com um Funcionário é obrigatório.");
        }
        if (dto.getCpf() != null && !DocumentoUtils.isCPF(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido!");
        }

        // 2. Busca a entidade existente pelo ID da URL (CORREÇÃO CRÍTICA)
        Familia entityExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Familiar com ID " + id + " não encontrado para atualização."));

        // 3. Busca o funcionário
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário com ID " + dto.getFuncionarioId() + " não encontrado."));

        // 4. Atualiza os campos manualmente (garante que não criamos um registro novo por acidente)
        // Ajuste os nomes dos setters (ex: setNome, setFamiliar) conforme estão na sua classe Familia
        entityExistente.setConjugue(dto.getConjugue());
        entityExistente.setCpf(dto.getCpf());
        entityExistente.setRg(dto.getRg());
        entityExistente.setTelefone(dto.getTelefone());
        entityExistente.setCelular(dto.getCelular());
        entityExistente.setDataNascimento(dto.getDataNascimento());
        entityExistente.setSexo(dto.getSexo());
        entityExistente.setFormacao(dto.getFormacao());
        entityExistente.setAtivo(dto.getAtivo());

        // 5. Reassocia o funcionário
        entityExistente.setFuncionario(funcionario);

        Familia saved = repository.save(entityExistente);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Familiar com ID " + id + " não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }
}