package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.PontoDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.PontoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Ponto;
import br.com.drs.radiotv_pro_escritorio.repository.PontoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PontoService {

    private final PontoRepository repository;
    private final PontoMapper mapper;

    @Transactional
    public PontoDTO salvar(PontoDTO dto) {
        Ponto entity = mapper.toEntity(dto);
        Ponto saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Ponto> listarTodos() {
        return  repository.findAll();
    }

    @Transactional
    public Optional<Ponto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public PontoDTO atualizar(Long id, PontoDTO dto) {
        Ponto existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ponto não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}