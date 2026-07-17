package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FeriadoDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.FeriadoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Feriado;
import br.com.drs.radiotv_pro_escritorio.repository.FeriadoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeriadoService {

    private final FeriadoRepository repository;
    private final FeriadoMapper mapper;

    @Transactional
    public FeriadoDTO salvar(FeriadoDTO dto) {
        Feriado entity = mapper.toEntity(dto);
        Feriado saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Feriado> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Feriado> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @PutMapping("/{id}")
    public FeriadoDTO atualizar(@PathVariable Long id, @RequestBody FeriadoDTO dto) {
        Feriado existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Feriado não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}