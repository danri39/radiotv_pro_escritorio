package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ComprasDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ComprasMapper;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.repository.ComprasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComprasService {

    private final ComprasRepository repository;
    private final ComprasMapper mapper;

    @Transactional
    public ComprasDTO salvar(ComprasDTO dto) {
        Compras entity = mapper.toEntity(dto);
        Compras saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Compras> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Compras> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ComprasDTO atualizar(Long id, ComprasDTO dto) {
        Compras comprasExistentes = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Compra não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, comprasExistentes);
        repository.save(comprasExistentes);
        return mapper.toDTO(comprasExistentes);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
