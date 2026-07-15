package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.VendedorDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.VendedorMapper;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import br.com.drs.radiotv_pro_escritorio.repository.VendedorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository repository;
    private final VendedorMapper mapper;

    @Transactional
    public VendedorDTO salvar(VendedorDTO dto) {

        Vendedor entity = mapper.toEntity(dto);
        Funcionario funcionario = repository.findById(dto.getFuncionario().getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado.")).getFuncionario();

        entity.setFuncionario(funcionario);
        Vendedor saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Vendedor> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Vendedor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public VendedorDTO atualizar(Long id, VendedorDTO dto) {
        Vendedor entity = mapper.toEntity(dto);
        Funcionario funcionario = repository.findById(dto.getFuncionario().getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado.")).getFuncionario();
        entity.setFuncionario(funcionario);
        Vendedor saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
