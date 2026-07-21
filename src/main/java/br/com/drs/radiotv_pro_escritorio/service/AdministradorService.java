package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.AdministradorDTO;
import br.com.drs.radiotv_pro_escritorio.model.Configuracao;
import br.com.drs.radiotv_pro_escritorio.model.enuns.*;
import br.com.drs.radiotv_pro_escritorio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final ContratoRepository contratoRepository;
    private final ContratoPagamentoRepository contratoPagamentoRepository;
    private final VendedorRepository vendedorRepository;
    private final AgenciaRepository agenciaRepository;
    private final PagamentosRepository pagamentosRepository;
    private final FolhaPagamentoRepository folhaPagamentoRepository;
    private final ComissaoVendedorRepository comissaoVendedorRepository;
    private final ComprasRepository comprasRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final LancamentoBeneficioMensalRepository lancamentoBeneficioRepository;

    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MM/yyyy");

    /**
     * Gera a visão 360° do sistema para o Administrador.
     */
    @Transactional(readOnly = true)
    public AdministradorDTO gerarVisaoCompleta(String mesReferencia) {
        if (mesReferencia == null || mesReferencia.isBlank()) {
            mesReferencia = LocalDate.now().format(FORMATO_MES);
        }

        log.info("Gerando visão administrativa completa para o mês {}", mesReferencia);

        LocalDate primeiroDia = getPrimeiroDiaDoMes(mesReferencia);
        LocalDate ultimoDia = getUltimoDiaDoMes(mesReferencia);

        // ==========================================
        // 1. COMERCIAL
        // ==========================================
        // CORREÇÃO: Usando findAll() e filtrando manualmente se findByAtivoTrue() não existir
        Long totalContratosAtivos = contratoRepository.findAll().stream()
                .filter(c -> c.getAtivo() != null && c.getAtivo())
                .count();

        Long totalContratosInadimplentes = contratoPagamentoRepository.findByStatusRecebimento(StatusRecebimento.ATRASADO)
                .stream().map(cp -> cp.getContrato().getContratoId()).distinct().count();

        // CORREÇÃO: Usando findAll() e filtrando
        BigDecimal valorTotalContratosAtivos = contratoRepository.findAll().stream()
                .filter(c -> c.getAtivo() != null && c.getAtivo())
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // CORREÇÃO: Usando findAll() e filtrando
        Long totalVendedoresAtivos = vendedorRepository.findAll().stream()
                .filter(v -> v.getFuncionario() != null && v.getFuncionario().getAtivo() != null && v.getFuncionario().getAtivo())
                .count();

        Long totalAgenciasAtivas = agenciaRepository.findAll().stream()
                .filter(a -> a.getAtivo() != null && a.getAtivo())
                .count();

        // ==========================================
        // 2. FINANCEIRO - RECEITAS
        // ==========================================
        BigDecimal totalFaturadoMes = contratoPagamentoRepository.buscarPorPeriodoVencimento(primeiroDia, ultimoDia)
                .stream()
                .filter(cp -> cp.getStatusRecebimento() == StatusRecebimento.FATURADO ||
                        cp.getStatusRecebimento() == StatusRecebimento.RECEBIDO)
                .map(cp -> cp.getValorParcela())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecebidoMes = contratoPagamentoRepository.buscarPorPeriodoRecebimento(primeiroDia, ultimoDia)
                .stream()
                .filter(cp -> cp.getStatusRecebimento() == StatusRecebimento.RECEBIDO && cp.getValorEfetivoPago() != null)
                .map(cp -> cp.getValorEfetivoPago())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceberAtrasado = contratoPagamentoRepository.findByStatusRecebimento(StatusRecebimento.ATRASADO)
                .stream()
                .map(cp -> cp.getValorParcela())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long quantidadeParcelasEmAtraso = contratoPagamentoRepository.findByStatusRecebimento(StatusRecebimento.ATRASADO)
                .stream().count();

        // ==========================================
        // 3. FINANCEIRO - DESPESAS
        // ==========================================
        BigDecimal totalFolhaPagaMes = folhaPagamentoRepository.somarTotalLiquidoPagoPorMes(mesReferencia);
        BigDecimal totalComissoesVendedoresMes = folhaPagamentoRepository.somarTotalComissoesPagasPorMes(mesReferencia);

        BigDecimal totalComissoesAgenciasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.COMISSAO_AGENCIA, primeiroDia, ultimoDia)
                .stream()
                .filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComprasAprovadasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.COMPRA_APROVADA, primeiroDia, ultimoDia)
                .stream()
                .filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalContasDiversasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.CONTA_DIVERSSA, primeiroDia, ultimoDia)
                .stream()
                .filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesasMes = totalFolhaPagaMes.add(totalComissoesVendedoresMes)
                .add(totalComissoesAgenciasMes).add(totalComprasAprovadasMes).add(totalContasDiversasMes);

        // ==========================================
        // 4. BANCOS (da Configuração)
        // ==========================================
        Configuracao config = configuracaoRepository.findAll().stream().findFirst().orElse(null);
        String agenciaPadrao = config != null ? config.getAgenciaPadrao() : null;
        String contaCorrentePadrao = config != null ? config.getContaCorrentePadrao() : null;
        String carteiraPadrao = config != null ? config.getCarteiraPadrao() : null;

        // ==========================================
        // 5. FLUXO DE CAIXA
        // ==========================================
        BigDecimal saldoFluxoCaixa = totalRecebidoMes.subtract(totalDespesasMes);

        // ==========================================
        // 6. RH
        // ==========================================
        Long totalFuncionariosAtivos = funcionarioRepository.findAll().stream()
                .filter(f -> f.getAtivo() != null && f.getAtivo())
                .count();
        Long totalFolhasAbertas = folhaPagamentoRepository.findByStatusFolhaAndAtivaTrue(StatusFolha.ABERTA).stream().count();
        Long totalFolhasFechadas = folhaPagamentoRepository.findByStatusFolhaAndAtivaTrue(StatusFolha.FECHADA).stream().count();
        Long totalFolhasPagas = folhaPagamentoRepository.findByStatusFolhaAndAtivaTrue(StatusFolha.PAGA).stream().count();

        // ==========================================
        // 7. PAGAMENTOS A FAZER
        // ==========================================
        Long totalPagamentosPendentes = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.PENDENTE).stream().count();
        Long totalPagamentosAguardandoDocumento = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.AGUARDANDO_DOCUMENTO).stream().count();
        Long totalPagamentosProntosParaPagar = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.PRONTO_PARA_PAGAMENTO).stream().count();

        BigDecimal valorTotalPagamentosPendentes = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.PENDENTE).stream()
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.AGUARDANDO_DOCUMENTO).stream()
                        .map(p -> p.getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .add(pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.PRONTO_PARA_PAGAMENTO).stream()
                        .map(p -> p.getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        // ==========================================
        // 8. COMPRAS
        // ==========================================
        Long totalComprasPendentesAprovacao = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.PENDENTE).stream().count();
        Long totalComprasAprovadas = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.APROVADA).stream().count();
        Long totalComprasRecusadas = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.RECUSADA).stream().count();
        Long totalComprasPagas = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.PAGA).stream().count();

        // ==========================================
        // 9. INDICADORES GERAIS
        // ==========================================
        Long totalComissoesVendedorPendentes = comissaoVendedorRepository.countByStatusComissaoAndAtivaTrue(StatusComissao.PENDENTE);
        Long totalBeneficiosAtivos = lancamentoBeneficioRepository.buscarPorMesReferencia(mesReferencia).stream().count();

        // ==========================================
        // MONTAR E RETORNAR DTO
        // ==========================================
        return AdministradorDTO.builder()
                .mesReferencia(mesReferencia)
                // Comercial
                .totalContratosAtivos(totalContratosAtivos)
                .totalContratosInadimplentes(totalContratosInadimplentes)
                .valorTotalContratosAtivos(valorTotalContratosAtivos)
                .totalVendedoresAtivos(totalVendedoresAtivos)
                .totalAgenciasAtivas(totalAgenciasAtivas)
                // Receitas
                .totalFaturadoMes(totalFaturadoMes)
                .totalRecebidoMes(totalRecebidoMes)
                .totalReceberAtrasado(totalReceberAtrasado)
                .quantidadeParcelasEmAtraso(quantidadeParcelasEmAtraso)
                // Despesas
                .totalFolhaPagaMes(totalFolhaPagaMes)
                .totalComissoesVendedoresMes(totalComissoesVendedoresMes)
                .totalComissoesAgenciasMes(totalComissoesAgenciasMes)
                .totalComprasAprovadasMes(totalComprasAprovadasMes)
                .totalContasDiversasMes(totalContasDiversasMes)
                .totalDespesasMes(totalDespesasMes)
                // Bancos
                .agenciaPadrao(agenciaPadrao)
                .contaCorrentePadrao(contaCorrentePadrao)
                .carteiraPadrao(carteiraPadrao)
                // Fluxo de Caixa
                .saldoFluxoCaixa(saldoFluxoCaixa)
                // RH
                .totalFuncionariosAtivos(totalFuncionariosAtivos)
                .totalFolhasAbertas(totalFolhasAbertas)
                .totalFolhasFechadas(totalFolhasFechadas)
                .totalFolhasPagas(totalFolhasPagas)
                // Pagamentos
                .totalPagamentosPendentes(totalPagamentosPendentes)
                .totalPagamentosAguardandoDocumento(totalPagamentosAguardandoDocumento)
                .totalPagamentosProntosParaPagar(totalPagamentosProntosParaPagar)
                .valorTotalPagamentosPendentes(valorTotalPagamentosPendentes)
                // Compras
                .totalComprasPendentesAprovacao(totalComprasPendentesAprovacao)
                .totalComprasAprovadas(totalComprasAprovadas)
                .totalComprasRecusadas(totalComprasRecusadas)
                .totalComprasPagas(totalComprasPagas)
                // Indicadores
                .totalComissoesVendedorPendentes(totalComissoesVendedorPendentes)
                .totalBeneficiosAtivos(totalBeneficiosAtivos)
                .build();
    }

    // ==========================================
    // MÉTODOS HELPERS DE DATA
    // ==========================================
    private LocalDate getPrimeiroDiaDoMes(String mesReferencia) {
        String[] partes = mesReferencia.split("/");
        return LocalDate.of(Integer.parseInt(partes[1]), Integer.parseInt(partes[0]), 1);
    }

    private LocalDate getUltimoDiaDoMes(String mesReferencia) {
        LocalDate primeiroDia = getPrimeiroDiaDoMes(mesReferencia);
        return primeiroDia.withDayOfMonth(primeiroDia.lengthOfMonth());
    }
}