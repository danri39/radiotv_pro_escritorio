package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoPagamentoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoPagamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class ContratoPagamentoService {

    private final ContratoPagamentoRepository repository;
    private final ContratoPagamentoMapper mapper;

    @Transactional
    public ContratoPagamentoDTO salvar(ContratoPagamentoDTO dto) {
        Contrato contratoExistente = repository.findById(dto.getContrato().getContratoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Contrato não encontrado com o ID: " + id)).getContrato();
        ContratoPagamento entity = mapper.toEntity(dto);
        ContratoPagamento saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<ContratoPagamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<ContratoPagamento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContratoPagamentoDTO atualizar(Long id, ContratoPagamentoDTO dto) {
        ContratoPagamento contratoPg = repository.findById(dto.getContratoPagamentoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pagamentos não encontrados com este id: " + id));
        mapper.updateEntityFromDto(dto, contratoPg);
        repository.save(contratoPg);
        return mapper.toDTO(contratoPg);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
