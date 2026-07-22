package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.BeneficiosDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.BeneficiosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Beneficios;
import br.com.drs.radiotv_pro_escritorio.repository.BeneficiosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeneficiosService {

    private final BeneficiosRepository repository;
    private final BeneficiosMapper mapper;

    @Transactional
    public BeneficiosDTO salvar(BeneficiosDTO dto) {
        Beneficios entity = mapper.toEntity(dto);
        Beneficios saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Beneficios> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Beneficios> buscarProId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public BeneficiosDTO atualizar(Long id, BeneficiosDTO dto) {
        Beneficios beneficiosExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Beneficio não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, beneficiosExistente);
        repository.save(beneficiosExistente);
        return mapper.toDTO(beneficiosExistente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
