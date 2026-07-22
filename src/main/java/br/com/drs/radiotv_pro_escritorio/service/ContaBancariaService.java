package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContaBancariaDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ContaBancariaMapper;
import br.com.drs.radiotv_pro_escritorio.model.ContaBancaria;
import br.com.drs.radiotv_pro_escritorio.repository.ContaBancariaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaBancariaService {

    private final ContaBancariaRepository repository;
    private final ContaBancariaMapper mapper;

    @Transactional
    public ContaBancariaDTO salvar(ContaBancariaDTO dto) {
        ContaBancaria entity = mapper.toEntity(dto);
        ContaBancaria saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<ContaBancaria> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<ContaBancaria> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContaBancariaDTO atualizar(Long id, ContaBancariaDTO dto){
        ContaBancaria existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta bancaria não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}