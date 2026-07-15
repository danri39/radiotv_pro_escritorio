package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.AgenciaDTO;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.AgenciaMapper;
import br.com.drs.radiotv_pro_escritorio.model.Agencia;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import br.com.drs.radiotv_pro_escritorio.repository.AgenciaRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgenciaService {

    private final AgenciaRepository repository;
    private final AgenciaMapper mapper;

    @Transactional
    public AgenciaDTO salvar(AgenciaDTO dto) {
        TipoPessoa tipo = dto.getTipoPessoa();

        if(TipoPessoa.FISICA.equals(tipo)) {
            if(!DocumentoUtils.isCPF(dto.getCpf())) {
                throw new RegraNegocioException("CPF inválido!");
            }
        } else if (TipoPessoa.JURIDICA.equals(tipo)) {
            if(!DocumentoUtils.isCNPJ(dto.getCnpj())) {
                throw new RegraNegocioException("CNPJ inválido!");
            }
        } else {
            throw new RegraNegocioException("Tipo de pessoa inválido: " + (tipo != null ? tipo.name() : "bull"));
        }

        Agencia entity = mapper.toEntity(dto);
        Agencia saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Agencia> listaTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Agencia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public AgenciaDTO atualizar(Long id, AgenciaDTO dto) {
        TipoPessoa tipo = dto.getTipoPessoa();

        if(TipoPessoa.FISICA.equals(tipo)) {
            if(!DocumentoUtils.isCPF(dto.getCpf())) {
                throw new RegraNegocioException("CPF Inválido!");
            } else if (TipoPessoa.JURIDICA.equals(tipo)) {
                if(!DocumentoUtils.isCNPJ(dto.getCnpj())) {
                    throw new RegraNegocioException("CNPJ Inválido");
                }
            } else {
                throw  new RegraNegocioException("Tipo de pessoa inválido: " + (tipo != null ? tipo.name() : "bull"));
            }
        }
        Agencia entity = mapper.toEntity(dto);
        Agencia saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
