package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import br.com.drs.radiotv_pro_escritorio.repository.VendedorRepository;
import br.com.drs.radiotv_pro_escritorio.dto.VendedorDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.VendedorMapper;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final VendedorMapper mapper;

    @Transactional
    public VendedorDTO salvar(VendedorDTO dto) {
        Vendedor entity = mapper.toEntity(dto);

        // CORREÇÃO: Usar funcionarioRepository para buscar o Funcionário
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado com o ID: " + dto.getFuncionarioId()));

        entity.setFuncionario(funcionario);
        Vendedor saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<Vendedor> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Vendedor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public VendedorDTO atualizar(Long id, VendedorDTO dto) {
        // CORREÇÃO: Buscar o vendedor existente pelo ID da rota
        Vendedor entity = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Vendedor não encontrado com o ID: " + id));

        // CORREÇÃO: Buscar o funcionário correto
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado com o ID: " + dto.getFuncionarioId()));

        entity.setFuncionario(funcionario);
        entity.setMetaMes(dto.getMetaMes());
        entity.setMesAno(dto.getMesAno());
        entity.setVendasMes(dto.getVendasMes());
        entity.setVendasTotal(dto.getVendasTotal());
        entity.setComissaoVendas(dto.getComissaoVendas());

        Vendedor saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}