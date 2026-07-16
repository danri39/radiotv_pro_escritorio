package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FuncionarioDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.FuncionarioMapper;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuncionarioService  {

    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;

    @Transactional
    public FuncionarioDTO salvar(FuncionarioDTO dto) {
        if(repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegraNegocioException("E-mail já cadastrado no sistema.");
        }
        TipoPessoa tipo = dto.getTipoPessoa();
        if(TipoPessoa.FISICA.equals(tipo)) {
            if(!DocumentoUtils.isCPF(dto.getCpf())) {
                throw new RegraNegocioException("CPF inválido!");
            }
        } else if(TipoPessoa.JURIDICA.equals(tipo)) {
            if(!DocumentoUtils.isCNPJ(dto.getCnpj())) {
                throw new RegraNegocioException("CNPJ inválido!");
            }
        } else {
            throw new RegraNegocioException("Tipo de pessoa inválido: " + (tipo != null ? tipo.name() : "bull"));
        }
        Funcionario entity = mapper.toEntity(dto);
        Funcionario saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<FuncionarioDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional
    public Optional<FuncionarioDTO> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    @Transactional
    public FuncionarioDTO atualizar(FuncionarioDTO dto, Long id) {
        Funcionario funcionarioExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado com o ID: " + id));
        // The mapper expects the entity itself as the @MappingTarget, not an Optional
        mapper.updateEntityFromDto(dto, funcionarioExistente);
        repository.save(funcionarioExistente);
        return mapper.toDTO(funcionarioExistente);
    }

    public void apagar(Long id) {
        repository.deleteById(id);
    }

    public List<FuncionarioDTO> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome não pode ser vazio.");
        }
        List<Funcionario> funcionarios = repository.findByNomeContainingIgnoreCase(nome.trim());
        if (funcionarios.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Funcionário não encontrado.");
        }
        return funcionarios.stream()
                .map(mapper::toDTO)
                .toList();
    }
}