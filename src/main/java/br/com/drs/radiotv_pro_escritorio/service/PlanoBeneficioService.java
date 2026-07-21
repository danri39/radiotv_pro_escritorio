package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.PlanoBeneficioDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.PlanoBeneficioMapper;
import br.com.drs.radiotv_pro_escritorio.model.PlanoBeneficio;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficio;
import br.com.drs.radiotv_pro_escritorio.repository.PlanoBeneficioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanoBeneficioService {

    private final PlanoBeneficioRepository repository;
    private final PlanoBeneficioMapper mapper;

    // ==========================================
    // 1. CRIAR PLANO DE BENEFÍCIO (Administrador/RH)
    // ==========================================
    @Transactional
    public PlanoBeneficioDTO criar(PlanoBeneficioDTO dto) {
        validarDadosBasicos(dto);

        // Verifica duplicidade pelo nome
        if (repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RuntimeException("Já existe um plano de benefício com o nome: " + dto.getNome());
        }

        PlanoBeneficio plano = mapper.toEntity(dto);
        plano.setAtivo(true);

        PlanoBeneficio salvo = repository.save(plano);
        log.info("Plano de benefício criado: {} - R$ {}/mês", salvo.getNome(), salvo.getValorMensalFixo());
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 2. LISTAGENS
    // ==========================================
    @Transactional(readOnly = true)
    public List<PlanoBeneficioDTO> listarTodos() {
        return mapper.toDTOList(repository.findByAtivoTrueOrderByTipoBeneficioAscNomeAsc());
    }

    @Transactional(readOnly = true)
    public PlanoBeneficioDTO buscarPorId(Long id) {
        PlanoBeneficio plano = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de benefício não encontrado com ID: " + id));
        return mapper.toDTO(plano);
    }

    @Transactional(readOnly = true)
    public List<PlanoBeneficioDTO> listarPorTipo(TipoBeneficio tipo) {
        return mapper.toDTOList(repository.findByTipoBeneficioAndAtivoTrue(tipo));
    }

    @Transactional(readOnly = true)
    public List<PlanoBeneficioDTO> listarPorOperadora(String operadora) {
        return mapper.toDTOList(repository.findByOperadoraContainingIgnoreCaseAndAtivoTrue(operadora));
    }

    // ==========================================
    // 3. EDITAR PLANO DE BENEFÍCIO
    // ==========================================
    @Transactional
    public PlanoBeneficioDTO editar(Long id, PlanoBeneficioDTO dto) {
        PlanoBeneficio plano = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de benefício não encontrado com ID: " + id));

        validarDadosBasicos(dto);

        // Verifica duplicidade (exceto o próprio plano)
        if (repository.existsByNomeIgnoreCase(dto.getNome()) && !plano.getNome().equalsIgnoreCase(dto.getNome())) {
            throw new RuntimeException("Já existe um plano de benefício com o nome: " + dto.getNome());
        }

        mapper.updateEntityFromDto(dto, plano);
        PlanoBeneficio salvo = repository.save(plano);

        log.info("Plano de benefício atualizado: ID {} - Novo valor: R$ {}/mês", id, plano.getValorMensalFixo());
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 4. INATIVAR PLANO (Soft Delete)
    // ==========================================
    @Transactional
    public void inativar(Long id) {
        PlanoBeneficio plano = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de benefício não encontrado com ID: " + id));

        plano.setAtivo(false);
        repository.save(plano);
        log.info("Plano de benefício inativado: ID {} - {}", id, plano.getNome());
    }

    // ==========================================
    // 5. REATIVAR PLANO
    // ==========================================
    @Transactional
    public void reativar(Long id) {
        PlanoBeneficio plano = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de benefício não encontrado com ID: " + id));

        plano.setAtivo(true);
        repository.save(plano);
        log.info("Plano de benefício reativado: ID {} - {}", id, plano.getNome());
    }

    // ==========================================
    // MÉTODO HELPER: Validação de dados básicos
    // ==========================================
    private void validarDadosBasicos(PlanoBeneficioDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RuntimeException("O nome do plano é obrigatório.");
        }
        if (dto.getTipoBeneficio() == null) {
            throw new RuntimeException("O tipo de benefício é obrigatório.");
        }
        if (dto.getValorMensalFixo() == null || dto.getValorMensalFixo().doubleValue() <= 0) {
            throw new RuntimeException("O valor mensal fixo deve ser maior que zero.");
        }
    }
}