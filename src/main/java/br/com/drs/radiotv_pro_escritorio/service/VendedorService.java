package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.VendedorDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.VendedorMapper;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import br.com.drs.radiotv_pro_escritorio.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final VendedorMapper mapper;

    @Transactional
    public VendedorDTO salvar(VendedorDTO dto) {
        log.info(">>> Recebendo DTO para salvar. funcionarioId: {}", dto.getFuncionarioId());

        if (dto.getFuncionarioId() == null) {
            throw new IllegalArgumentException("O ID do funcionário é obrigatório e não pode ser nulo.");
        }

        // Busca no repositório CORRETO
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado com ID: " + dto.getFuncionarioId()));

        // O mapper preenche os campos simples (metaMes, mesAno, etc)
        Vendedor entity = mapper.toEntity(dto);

        // Forçamos a relação com o funcionário buscado
        entity.setFuncionario(funcionario);
        entity.setVendedorId(null); // Garante que o Hibernate fará um INSERT, não UPDATE

        Vendedor saved = vendedorRepository.save(entity);
        log.info(">>> Vendedor salvo com sucesso. ID: {}", saved.getVendedorId());

        return mapper.toDTO(saved);
    }

    @Transactional
    public VendedorDTO atualizar(Long id, VendedorDTO dto) {
        log.info(">>> Atualizando vendedor ID: {}. funcionarioId: {}", id, dto.getFuncionarioId());

        Vendedor entity = vendedorRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Vendedor não encontrado com ID: " + id));

        if (dto.getFuncionarioId() != null) {
            Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado com ID: " + dto.getFuncionarioId()));
            entity.setFuncionario(funcionario);
        }

        entity.setMetaMes(dto.getMetaMes());
        entity.setMesAno(dto.getMesAno());
        entity.setVendasMes(dto.getVendasMes());
        entity.setVendasTotal(dto.getVendasTotal());
        entity.setComissaoVendas(dto.getComissaoVendas());

        Vendedor saved = vendedorRepository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<Vendedor> listarTodos() {
        return vendedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Vendedor> buscarPorId(Long id) {
        return vendedorRepository.findById(id);
    }

    @Transactional
    public void apagar(Long id) {
        vendedorRepository.deleteById(id);
    }
}