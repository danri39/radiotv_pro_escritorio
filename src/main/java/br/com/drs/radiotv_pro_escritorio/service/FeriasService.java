package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FeriasDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.FeriasMapper;
import br.com.drs.radiotv_pro_escritorio.model.Ferias;
import br.com.drs.radiotv_pro_escritorio.repository.FeriasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeriasService {

    private final FeriasRepository repository;
    private final FeriasMapper mapper;

    @Transactional
    public FeriasDTO salvar(FeriasDTO dto) {
        Ferias entity = mapper.toEntity(dto);
        Ferias saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Ferias> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Ferias> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public FeriasDTO atualizar(Long id, FeriasDTO dto) {
        Ferias existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Férias não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}