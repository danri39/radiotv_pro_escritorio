package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.HorariosBreaksDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.HorariosBreaksMapper;
import br.com.drs.radiotv_pro_escritorio.model.HorariosBreaks;
import br.com.drs.radiotv_pro_escritorio.repository.HorariosBreaksRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HorariosBreaksService {

    private final HorariosBreaksRepository repository;
    private final HorariosBreaksMapper mapper;

    @Transactional
    public HorariosBreaksDTO salvar(HorariosBreaksDTO dto) {
        HorariosBreaks entity = mapper.toEntity(dto);
        HorariosBreaks saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<HorariosBreaks> listarTodos(){
        return repository.findAll();
    }

    @Transactional
    public Optional<HorariosBreaks> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public HorariosBreaksDTO atualizar(Long id, HorariosBreaksDTO dto) {
        HorariosBreaks existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Horários não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
