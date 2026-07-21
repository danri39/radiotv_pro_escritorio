package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoPagamentoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoPagamentoRepository;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // TODO: Injetar quando criarmos essas classes
    // private final ComissaoVendedorService comissaoVendedorService;
    // private final PagamentoService pagamentoService;

    // ==========================================
    // 1. GERAR PARCELAS AUTOMATICAMENTE (Gatilho ao criar contrato)
    // ==========================================
    /**
     * Este método é chamado pelo ContratoService quando um contrato é criado.
     * Gera N parcelas baseadas na quantidadeParcelas e valorParcelas do contrato.
     */
    @Transactional
    public List<ContratoPagamentoDTO> gerarParcelasDoContrato(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com ID: " + contratoId));

        // Evita duplicação
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
                    .dataVencimento(dataBase.plusMonths(i - 1)) // Parcela 1 = mês atual, 2 = +1 mês, etc.
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
    // 3. FATURAR PARCELA (Escritório emite boleto/nota)
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
    // 4. DAR BAIXA / RECEBER PAGAMENTO (Escritório)
    // ==========================================
    /**
     * Este é o método MAIS IMPORTANTE do fluxo financeiro.
     * Ao dar baixa, o sistema:
     * 1. Marca a parcela como RECEBIDA
     * 2. Dispara o cálculo da comissão do vendedor (cria ComissaoVendedor)
     * 3. Dispara o cálculo da comissão da agência (cria lançamento em Pagamento)
     */
    @Transactional
    public ContratoPagamentoDTO receberPagamento(Long id, BigDecimal valorRecebido) {
        ContratoPagamento parcela = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada com ID: " + id));

        if (valorRecebido == null || valorRecebido.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor recebido deve ser maior que zero.");
        }

        // Marca como recebida (método helper da entidade valida o status)
        parcela.marcarComoRecebido(LocalDate.now(), valorRecebido);
        ContratoPagamento salva = repository.save(parcela);

        log.info("Parcela {} do contrato {} recebida. Valor: R$ {}",
                parcela.getNumeroParcela(), parcela.getContrato().getContratoId(), valorRecebido);

        // ==========================================
        // TRIGGER: DISPARAR COMISSÕES
        // ==========================================
        dispararComissoes(parcela, valorRecebido);

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 5. MÉTODO INTERNO: Disparar Comissões
    // ==========================================
    /**
     * Calcula e lança as comissões do vendedor e da agência.
     * A comissão é sempre sobre o VALOR EFETIVAMENTE RECEBIDO (nunca sobre valor futuro).
     */
    private void dispararComissoes(ContratoPagamento parcela, BigDecimal valorRecebido) {
        Contrato contrato = parcela.getContrato();

        // --- COMISSÃO DO VENDEDOR ---
        if (!parcela.getComissaoVendedorLancada()) {
            int percentualVendedor = contrato.getVendedor().getComissaoVendas(); // Ex: 10
            BigDecimal comissaoVendedor = valorRecebido
                    .multiply(BigDecimal.valueOf(percentualVendedor))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // TODO: Quando criarmos o ComissaoVendedorService, descomentar:
            // comissaoVendedorService.lancarComissao(
            //     contrato.getVendedor(),
            //     parcela,
            //     comissaoVendedor,
            //     LocalDate.now()
            // );

            parcela.setComissaoVendedorLancada(true);
            log.info("Comissão do vendedor lançada: R$ {} ({}%)", comissaoVendedor, percentualVendedor);
        }

        // --- COMISSÃO DA AGÊNCIA (só se houver agência no contrato) ---
        if (contrato.getAgencia() != null && !parcela.getComissaoAgenciaLancada()) {
            int percentualAgencia = contrato.getAgencia().getComissaoVendas(); // Ex: 20
            BigDecimal comissaoAgencia = valorRecebido
                    .multiply(BigDecimal.valueOf(percentualAgencia))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // TODO: Quando criarmos o PagamentoService, descomentar:
            // pagamentoService.lancarComissaoAgencia(
            //     contrato.getAgencia(),
            //     parcela,
            //     comissaoAgencia,
            //     LocalDate.now()
            // );

            parcela.setComissaoAgenciaLancada(true);
            log.info("Comissão da agência lançada: R$ {} ({}%)", comissaoAgencia, percentualAgencia);
        }

        repository.save(parcela);
    }

    // ==========================================
    // 6. JOB: Marcar parcelas vencidas como ATRASADO
    // ==========================================
    /**
     * Este método deve ser chamado por um @Scheduled job que roda diariamente.
     * Varre todas as parcelas vencidas e não recebidas e marca como ATRASADO.
     */
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
    // 7. CANCELAR PARCELA (Administrador)
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
    // 8. LISTAR PENDÊNCIAS DE COMISSÃO (Painel Admin)
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