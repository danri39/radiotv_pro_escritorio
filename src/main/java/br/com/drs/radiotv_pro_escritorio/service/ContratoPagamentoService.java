package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoPagamentoMapper;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoPagamentoRepository;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContratoPagamentoService {

    private final ContratoPagamentoRepository repository;
    private final ContratoRepository contratoRepository;
    private final ContratoPagamentoMapper mapper;
    private final ComissaoVendedorService comissaoVendedorService;
    private final PagamentosService pagamentosService;

    // ==========================================
    // 1. GERAR PARCELAS DO CONTRATO
    // ==========================================
    @Transactional
    public List<ContratoPagamentoDTO> gerarParcelasDoContrato(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com ID: " + contratoId));

        if (repository.existsByContrato_ContratoId(contratoId)) {
            throw new RuntimeException("Já existem parcelas geradas para este contrato.");
        }

        if (contrato.getQuantidadeParcelas() == null || contrato.getQuantidadeParcelas() <= 0) {
            throw new RuntimeException("O contrato deve ter uma quantidade de parcelas válida.");
        }

        if (contrato.getDataPrimeiroPagamento() == null) {
            throw new RuntimeException("O contrato deve ter uma data de primeiro pagamento definida.");
        }

        List<ContratoPagamento> parcelas = new ArrayList<>();
        LocalDate dataBase = contrato.getDataPrimeiroPagamento();

        for (int i = 1; i <= contrato.getQuantidadeParcelas(); i++) {
            ContratoPagamento parcela = ContratoPagamento.builder()
                    .contrato(contrato)
                    .numeroParcela(i)
                    .dataVencimento(dataBase.plusMonths(i - 1))
                    .valorParcela(contrato.getValorParcelas())
                    .statusRecebimento(StatusRecebimento.A_FATURAR)
                    .comissaoVendedorLancada(false)
                    .comissaoAgenciaLancada(false)
                    .ativo(true)
                    .build();
            parcelas.add(parcela);
        }

        List<ContratoPagamento> parcelasSalvas = repository.saveAll(parcelas);
        log.info("Geradas {} parcelas para o contrato ID {}", parcelasSalvas.size(), contratoId);
        return mapper.toDTOList(parcelasSalvas);
    }

    // ==========================================
    // 2. LISTAGENS
    // ==========================================
    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarTodas() {
        return mapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public ContratoPagamentoDTO buscarPorId(Long id) {
        ContratoPagamento parcela = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada com ID: " + id));
        return mapper.toDTO(parcela);
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarPorContrato(Long contratoId) {
        return mapper.toDTOList(repository.findByContrato_ContratoIdOrderByNumeroParcelaAsc(contratoId));
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarPorStatus(StatusRecebimento status) {
        return mapper.toDTOList(repository.findByStatusRecebimento(status));
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarPorPeriodoRecebimento(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoRecebimento(inicio, fim));
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarPorPeriodoVencimento(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoVencimento(inicio, fim));
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarPorVendedor(Long vendedorId) {
        return mapper.toDTOList(repository.buscarPorVendedor(vendedorId));
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarPorAgencia(Long agenciaId) {
        return mapper.toDTOList(repository.buscarPorAgencia(agenciaId));
    }

    // ==========================================
    // 3. FATURAR PARCELA
    // ==========================================
    @Transactional
    public ContratoPagamentoDTO faturarParcela(Long id, String numeroFatura) {
        ContratoPagamento parcela = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada com ID: " + id));

        if (numeroFatura == null || numeroFatura.isBlank()) {
            throw new RuntimeException("O número da fatura é obrigatório.");
        }

        parcela.marcarComoFaturado(numeroFatura);
        ContratoPagamento salva = repository.save(parcela);
        log.info("Parcela {} do contrato {} faturada com número {}",
                parcela.getNumeroParcela(), parcela.getContrato().getContratoId(), numeroFatura);
        return mapper.toDTO(salva);
    }

    // ==========================================
    // 4. RECEBER PAGAMENTO (COM GATILHO AUTOMÁTICO)
    // ==========================================
    @Transactional
    public ContratoPagamentoDTO receberPagamento(Long id, BigDecimal valorRecebido) {
        ContratoPagamento parcela = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada com ID: " + id));

        if (valorRecebido == null || valorRecebido.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor recebido deve ser maior que zero.");
        }

        parcela.marcarComoRecebido(LocalDate.now(), valorRecebido);
        ContratoPagamento salva = repository.save(parcela);

        log.info("Parcela {} do contrato {} recebida. Valor: R$ {}",
                parcela.getNumeroParcela(), parcela.getContrato().getContratoId(), valorRecebido);

        // DISPARA AS COMISSÕES AUTOMATICAMENTE
        dispararComissoes(parcela, valorRecebido);

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 5. MÉTODO INTERNO: DISPARAR COMISSÕES
    // ==========================================
    private void dispararComissoes(ContratoPagamento parcela, BigDecimal valorRecebido) {
        Contrato contrato = parcela.getContrato();

        // COMISSÃO DO VENDEDOR (Versão segura contra NullPointer)
        if (Boolean.FALSE.equals(parcela.getComissaoVendedorLancada()) && contrato.getVendedor() != null) {
            try {
                comissaoVendedorService.lancarComissao(contrato.getVendedor(), parcela, valorRecebido);
                parcela.setComissaoVendedorLancada(true);
                log.info("Comissão do vendedor lançada com sucesso para a parcela {}", parcela.getNumeroParcela());
            } catch (Exception e) {
                log.error("Falha ao lançar comissão do vendedor na parcela {}: {}", parcela.getNumeroParcela(), e.getMessage());
                // Não interrompe o recebimento, apenas registra o erro no log para o admin verificar
            }
        }

        // COMISSÃO DA AGÊNCIA
        if (!parcela.getComissaoAgenciaLancada() && contrato.getAgencia() != null) {
            int percentualAgencia = contrato.getAgencia().getComissaoVendas();
            BigDecimal valorComissaoAgencia = valorRecebido
                    .multiply(BigDecimal.valueOf(percentualAgencia))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            pagamentosService.lancarComissaoAgencia(contrato.getAgencia(), parcela, valorComissaoAgencia, LocalDate.now());
            parcela.setComissaoAgenciaLancada(true);
            log.info("Comissão da agência lançada para a parcela {}", parcela.getNumeroParcela());
        }

        repository.save(parcela);
    }

    // ==========================================
    // 6. JOB: MARCAR PARCELAS ATRASADAS
    // ==========================================
    @Transactional
    public int marcarParcelasAtrasadas() {
        LocalDate hoje = LocalDate.now();
        List<StatusRecebimento> statusPendentes = List.of(
                StatusRecebimento.A_FATURAR,
                StatusRecebimento.FATURADO
        );

        List<ContratoPagamento> parcelasVencidas = repository.buscarParcelasVencidasNaoRecebidas(hoje, statusPendentes);

        int count = 0;
        for (ContratoPagamento parcela : parcelasVencidas) {
            parcela.marcarComoAtrasado();
            repository.save(parcela);
            count++;
        }

        if (count > 0) {
            log.info("Job de atraso: {} parcelas marcadas como ATRASADO", count);
        }
        return count;
    }

    // ==========================================
    // 7. CANCELAR PARCELA
    // ==========================================
    @Transactional
    public void cancelarParcela(Long id, String motivo) {
        ContratoPagamento parcela = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada com ID: " + id));

        if (parcela.getStatusRecebimento() == StatusRecebimento.RECEBIDO) {
            throw new RuntimeException("Não é possível cancelar uma parcela já recebida.");
        }

        parcela.setStatusRecebimento(StatusRecebimento.CANCELADO);
        parcela.setAtivo(false);
        repository.save(parcela);
        log.info("Parcela {} do contrato {} cancelada. Motivo: {}",
                parcela.getNumeroParcela(), parcela.getContrato().getContratoId(), motivo);
    }

    // ==========================================
    // 8. LISTAR PENDÊNCIAS
    // ==========================================
    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarComissoesVendedorPendentes() {
        return mapper.toDTOList(repository.buscarParcelasComComissaoVendedorPendente());
    }

    @Transactional(readOnly = true)
    public List<ContratoPagamentoDTO> listarComissoesAgenciaPendentes() {
        return mapper.toDTOList(repository.buscarParcelasComComissaoAgenciaPendente());
    }
}