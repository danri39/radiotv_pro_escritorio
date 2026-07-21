package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.GerencialComercialDTO;
import br.com.drs.radiotv_pro_escritorio.dto.GerencialFinanceiroDTO;
import br.com.drs.radiotv_pro_escritorio.dto.GerencialRHDTO;
import br.com.drs.radiotv_pro_escritorio.dto.VendedorRankingDTO;
import br.com.drs.radiotv_pro_escritorio.model.Configuracao;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import br.com.drs.radiotv_pro_escritorio.model.enuns.*;
import br.com.drs.radiotv_pro_escritorio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerencialService {

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

    // ==========================================
    // 📊 VISÃO COMERCIAL
    // ==========================================
    @Transactional(readOnly = true)
    public GerencialComercialDTO gerarVisaoComercial(String mesReferencia) {
        if (mesReferencia == null || mesReferencia.isBlank()) {
            mesReferencia = LocalDate.now().format(FORMATO_MES);
        }

        log.info("Gerando visão comercial para o mês {}", mesReferencia);

        LocalDate primeiroDia = getPrimeiroDiaDoMes(mesReferencia);
        LocalDate ultimoDia = getUltimoDiaDoMes(mesReferencia);

        // CONTRATOS
        List<Contrato> contratosAtivos = contratoRepository.findAll().stream()
                .filter(c -> c.getAtivo() != null && c.getAtivo())
                .collect(Collectors.toList());

        Long totalContratosAtivos = (long) contratosAtivos.size();
        Long totalContratosInadimplentes = contratoPagamentoRepository.findByStatusRecebimento(StatusRecebimento.ATRASADO)
                .stream().map(cp -> cp.getContrato().getContratoId()).distinct().count();

        BigDecimal valorTotalContratosAtivos = contratosAtivos.stream()
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalReceber = contratoPagamentoRepository.findAll().stream()
                .filter(cp -> cp.getAtivo() != null && cp.getAtivo())
                .filter(cp -> cp.getStatusRecebimento() != StatusRecebimento.RECEBIDO &&
                        cp.getStatusRecebimento() != StatusRecebimento.CANCELADO)
                .map(cp -> cp.getValorParcela())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // VENDEDORES
        List<Vendedor> vendedoresAtivos = vendedorRepository.findAll().stream()
                .filter(v -> v.getFuncionario() != null && v.getFuncionario().getAtivo() != null && v.getFuncionario().getAtivo())
                .collect(Collectors.toList());

        Long totalVendedoresAtivos = (long) vendedoresAtivos.size();

        BigDecimal totalVendasMes = vendedoresAtivos.stream()
                .map(v -> v.getVendasMes() != null ? v.getVendasMes() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissoesVendedoresMes = folhaPagamentoRepository.somarTotalComissoesPagasPorMes(mesReferencia);

        // Ranking de vendedores
        List<VendedorRankingDTO> ranking = vendedoresAtivos.stream()
                .map(v -> {
                    BigDecimal vendas = v.getVendasMes() != null ? v.getVendasMes() : BigDecimal.ZERO;
                    BigDecimal meta = v.getMetaMes() != null ? v.getMetaMes() : BigDecimal.ZERO;
                    BigDecimal percentual = meta.compareTo(BigDecimal.ZERO) > 0
                            ? vendas.multiply(BigDecimal.valueOf(100)).divide(meta, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return VendedorRankingDTO.builder()
                            .vendedorId(v.getVendedorId())
                            .vendedorNome(v.getFuncionario().getNome())
                            .metaMes(meta)
                            .vendasMes(vendas)
                            .comissaoMes(v.getVendasMes() != null ?
                                    vendas.multiply(BigDecimal.valueOf(v.getComissaoVendas()))
                                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                                    : BigDecimal.ZERO)
                            .percentualAtingimento(percentual)
                            .build();
                })
                .sorted(Comparator.comparing(VendedorRankingDTO::getVendasMes).reversed())
                .collect(Collectors.toList());

        // Adiciona posição no ranking
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosicaoRanking(i + 1);
        }

        // AGÊNCIAS
        Long totalAgenciasAtivas = agenciaRepository.findAll().stream()
                .filter(a -> a.getAtivo() != null && a.getAtivo())
                .count();

        BigDecimal totalVendasAgenciasMes = agenciaRepository.findAll().stream()
                .filter(a -> a.getAtivo() != null && a.getAtivo())
                .map(a -> a.getVendasMes() != null ? a.getVendasMes() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissoesAgenciasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.COMISSAO_AGENCIA, primeiroDia, ultimoDia)
                .stream()
                .filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // METAS
        BigDecimal metaVendasMes = vendedoresAtivos.stream()
                .map(v -> v.getMetaMes() != null ? v.getMetaMes() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentualAtingimentoMeta = metaVendasMes.compareTo(BigDecimal.ZERO) > 0
                ? totalVendasMes.multiply(BigDecimal.valueOf(100))
                .divide(metaVendasMes, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ALERTAS
        Long contratosVencendoProximoMes = contratoRepository.findAll().stream()
                .filter(c -> c.getAtivo() != null && c.getAtivo())
                .filter(c -> c.getDataFinal() != null &&
                        c.getDataFinal().getMonth() == LocalDate.now().plusMonths(1).getMonth())
                .count();

        Long vendedoresAbaixoDaMeta = ranking.stream()
                .filter(r -> r.getPercentualAtingimento().compareTo(BigDecimal.valueOf(100)) < 0)
                .count();

        return GerencialComercialDTO.builder()
                .mesReferencia(mesReferencia)
                .totalContratosAtivos(totalContratosAtivos)
                .totalContratosInadimplentes(totalContratosInadimplentes)
                .valorTotalContratosAtivos(valorTotalContratosAtivos)
                .valorTotalReceber(valorTotalReceber)
                .totalVendedoresAtivos(totalVendedoresAtivos)
                .totalVendasMes(totalVendasMes)
                .totalComissoesVendedoresMes(totalComissoesVendedoresMes)
                .rankingVendedores(ranking)
                .totalAgenciasAtivas(totalAgenciasAtivas)
                .totalVendasAgenciasMes(totalVendasAgenciasMes)
                .totalComissoesAgenciasMes(totalComissoesAgenciasMes)
                .metaVendasMes(metaVendasMes)
                .vendasRealizadasMes(totalVendasMes)
                .percentualAtingimentoMeta(percentualAtingimentoMeta)
                .contratosVencendoProximoMes(contratosVencendoProximoMes)
                .vendedoresAbaixoDaMeta(vendedoresAbaixoDaMeta)
                .build();
    }

    // ==========================================
    // 💰 VISÃO FINANCEIRA
    // ==========================================
    @Transactional(readOnly = true)
    public GerencialFinanceiroDTO gerarVisaoFinanceira(String mesReferencia) {
        if (mesReferencia == null || mesReferencia.isBlank()) {
            mesReferencia = LocalDate.now().format(FORMATO_MES);
        }

        log.info("Gerando visão financeira para o mês {}", mesReferencia);

        LocalDate primeiroDia = getPrimeiroDiaDoMes(mesReferencia);
        LocalDate ultimoDia = getUltimoDiaDoMes(mesReferencia);

        // RECEITAS
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

        Long quantidadeParcelasAReceber = contratoPagamentoRepository.findAll().stream()
                .filter(cp -> cp.getAtivo() != null && cp.getAtivo())
                .filter(cp -> cp.getStatusRecebimento() != StatusRecebimento.RECEBIDO &&
                        cp.getStatusRecebimento() != StatusRecebimento.CANCELADO)
                .count();

        // DESPESAS
        BigDecimal totalFolhaPagaMes = folhaPagamentoRepository.somarTotalLiquidoPagoPorMes(mesReferencia);
        BigDecimal totalComissoesVendedoresMes = folhaPagamentoRepository.somarTotalComissoesPagasPorMes(mesReferencia);

        BigDecimal totalComissoesAgenciasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.COMISSAO_AGENCIA, primeiroDia, ultimoDia)
                .stream().filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComprasAprovadasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.COMPRA_APROVADA, primeiroDia, ultimoDia)
                .stream().filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalContasDiversasMes = pagamentosRepository.buscarPorTipoEPeriodoPagamento(
                        TipoPagamento.CONTA_DIVERSSA, primeiroDia, ultimoDia)
                .stream().filter(p -> p.getStatusPagamento() == StatusPagamento.PAGO)
                .map(p -> p.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesasMes = totalFolhaPagaMes.add(totalComissoesVendedoresMes)
                .add(totalComissoesAgenciasMes).add(totalComprasAprovadasMes).add(totalContasDiversasMes);

        // FLUXO DE CAIXA
        BigDecimal saldoFluxoCaixa = totalRecebidoMes.subtract(totalDespesasMes);

        // BANCOS
        Configuracao config = configuracaoRepository.findAll().stream().findFirst().orElse(null);

        // CONTAS A PAGAR
        Long totalPagamentosPendentes = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.PENDENTE).stream().count();
        Long totalPagamentosAguardandoDocumento = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.AGUARDANDO_DOCUMENTO).stream().count();
        Long totalPagamentosProntosParaPagar = pagamentosRepository.findByStatusPagamentoAndAtivoTrue(StatusPagamento.PRONTO_PARA_PAGAMENTO).stream().count();

        BigDecimal valorTotalPagamentosPendentes = pagamentosRepository.findAll().stream()
                .filter(p -> p.getAtivo() != null && p.getAtivo())
                .filter(p -> p.getStatusPagamento() != StatusPagamento.PAGO &&
                        p.getStatusPagamento() != StatusPagamento.CANCELADO)
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // COMPRAS
        Long totalComprasPendentesAprovacao = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.PENDENTE).stream().count();
        Long totalComprasAprovadasPendentesPagamento = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.APROVADA).stream().count();
        BigDecimal valorComprasPendentes = comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.APROVADA).stream()
                .map(c -> c.getValorTotalGeral() != null ? c.getValorTotalGeral() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // INDICADORES
        BigDecimal margemOperacional = totalRecebidoMes.compareTo(BigDecimal.ZERO) > 0
                ? totalRecebidoMes.subtract(totalDespesasMes)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalRecebidoMes, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal indiceInadimplencia = totalFaturadoMes.compareTo(BigDecimal.ZERO) > 0
                ? totalReceberAtrasado.multiply(BigDecimal.valueOf(100))
                .divide(totalFaturadoMes, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return GerencialFinanceiroDTO.builder()
                .mesReferencia(mesReferencia)
                .totalFaturadoMes(totalFaturadoMes)
                .totalRecebidoMes(totalRecebidoMes)
                .totalReceberAtrasado(totalReceberAtrasado)
                .quantidadeParcelasEmAtraso(quantidadeParcelasEmAtraso)
                .quantidadeParcelasAReceber(quantidadeParcelasAReceber)
                .totalFolhaPagaMes(totalFolhaPagaMes)
                .totalComissoesVendedoresMes(totalComissoesVendedoresMes)
                .totalComissoesAgenciasMes(totalComissoesAgenciasMes)
                .totalComprasAprovadasMes(totalComprasAprovadasMes)
                .totalContasDiversasMes(totalContasDiversasMes)
                .totalDespesasMes(totalDespesasMes)
                .saldoFluxoCaixa(saldoFluxoCaixa)
                .agenciaPadrao(config != null ? config.getAgenciaPadrao() : null)
                .contaCorrentePadrao(config != null ? config.getContaCorrentePadrao() : null)
                .carteiraPadrao(config != null ? config.getCarteiraPadrao() : null)
                .totalPagamentosPendentes(totalPagamentosPendentes)
                .totalPagamentosAguardandoDocumento(totalPagamentosAguardandoDocumento)
                .totalPagamentosProntosParaPagar(totalPagamentosProntosParaPagar)
                .valorTotalPagamentosPendentes(valorTotalPagamentosPendentes)
                .totalComprasPendentesAprovacao(totalComprasPendentesAprovacao)
                .totalComprasAprovadasPendentesPagamento(totalComprasAprovadasPendentesPagamento)
                .valorComprasPendentes(valorComprasPendentes)
                .margemOperacional(margemOperacional)
                .indiceInadimplencia(indiceInadimplencia)
                .build();
    }

    // ==========================================
    // 👥 VISÃO DE RH
    // ==========================================
    @Transactional(readOnly = true)
    public GerencialRHDTO gerarVisaoRH(String mesReferencia) {
        if (mesReferencia == null || mesReferencia.isBlank()) {
            mesReferencia = LocalDate.now().format(FORMATO_MES);
        }

        log.info("Gerando visão de RH para o mês {}", mesReferencia);

        // FUNCIONÁRIOS
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findAll().stream()
                .filter(f -> f.getAtivo() != null && f.getAtivo())
                .collect(Collectors.toList());

        Long totalFuncionariosAtivos = (long) funcionariosAtivos.size();
        Long totalFuncionariosVendedores = funcionariosAtivos.stream()
                .filter(f -> f.getVendedor() != null && f.getVendedor())
                .count();
        Long totalFuncionariosAdministrativos = totalFuncionariosAtivos - totalFuncionariosVendedores;

        BigDecimal totalFolhaSalarialMes = funcionariosAtivos.stream()
                .map(f -> f.getSalarioBruto() != null ? f.getSalarioBruto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // FOLHA DE PAGAMENTO
        Long totalFolhasAbertas = folhaPagamentoRepository.findByStatusFolhaAndAtivaTrue(StatusFolha.ABERTA).stream().count();
        Long totalFolhasFechadas = folhaPagamentoRepository.findByStatusFolhaAndAtivaTrue(StatusFolha.FECHADA).stream().count();
        Long totalFolhasPagas = folhaPagamentoRepository.findByStatusFolhaAndAtivaTrue(StatusFolha.PAGA).stream().count();

        BigDecimal totalLiquidoPagoMes = folhaPagamentoRepository.somarTotalLiquidoPagoPorMes(mesReferencia);
        BigDecimal totalComissoesPagasMes = folhaPagamentoRepository.somarTotalComissoesPagasPorMes(mesReferencia);
        BigDecimal totalBeneficiosDescontadosMes = folhaPagamentoRepository.somarTotalBeneficiosDescontadosPorMes(mesReferencia);

        // BENEFÍCIOS
        Long totalBeneficiosAtivos = lancamentoBeneficioRepository.buscarPorMesReferencia(mesReferencia).stream().count();
        BigDecimal totalValorBeneficiosMes = lancamentoBeneficioRepository.buscarPorMesReferencia(mesReferencia).stream()
                .map(l -> l.calcularTotalMensal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCoparticipacoesMes = lancamentoBeneficioRepository.buscarPorMesReferencia(mesReferencia).stream()
                .map(l -> l.getValorCoparticipacaoOuZero())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // COMISSÕES
        Long totalComissoesVendedorPendentes = comissaoVendedorRepository.countByStatusComissaoAndAtivaTrue(StatusComissao.PENDENTE);
        Long totalComissoesVendedorProcessadas = comissaoVendedorRepository.countByStatusComissaoAndAtivaTrue(StatusComissao.PROCESSADA);
        BigDecimal valorTotalComissoesPendentes = comissaoVendedorRepository.findByStatusComissaoAndAtivaTrue(StatusComissao.PENDENTE).stream()
                .map(c -> c.getValorComissao())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // INDICADORES
        BigDecimal custoMedioPorFuncionario = totalFuncionariosAtivos > 0
                ? totalLiquidoPagoMes.divide(BigDecimal.valueOf(totalFuncionariosAtivos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal percentualComissoesSobreFolha = totalLiquidoPagoMes.compareTo(BigDecimal.ZERO) > 0
                ? totalComissoesPagasMes.multiply(BigDecimal.valueOf(100))
                .divide(totalLiquidoPagoMes, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ALERTAS
        Long funcionariosSemFolhaNoMes = funcionariosAtivos.stream()
                .filter(f -> !folhaPagamentoRepository.existsByFuncionario_FuncionarioIdAndMesReferenciaAndAtivaTrue(
                        f.getId(), mesReferencia))
                .count();

        Long funcionariosComComissaoPendente = comissaoVendedorRepository.findByStatusComissaoAndAtivaTrue(StatusComissao.PENDENTE).stream()
                .map(c -> c.getVendedor().getFuncionario().getId())
                .distinct()
                .count();

        return GerencialRHDTO.builder()
                .mesReferencia(mesReferencia)
                .totalFuncionariosAtivos(totalFuncionariosAtivos)
                .totalFuncionariosVendedores(totalFuncionariosVendedores)
                .totalFuncionariosAdministrativos(totalFuncionariosAdministrativos)
                .totalFolhaSalarialMes(totalFolhaSalarialMes)
                .totalFolhasAbertas(totalFolhasAbertas)
                .totalFolhasFechadas(totalFolhasFechadas)
                .totalFolhasPagas(totalFolhasPagas)
                .totalLiquidoPagoMes(totalLiquidoPagoMes)
                .totalComissoesPagasMes(totalComissoesPagasMes)
                .totalBeneficiosDescontadosMes(totalBeneficiosDescontadosMes)
                .totalBeneficiosAtivos(totalBeneficiosAtivos)
                .totalValorBeneficiosMes(totalValorBeneficiosMes)
                .totalCoparticipacoesMes(totalCoparticipacoesMes)
                .totalComissoesVendedorPendentes(totalComissoesVendedorPendentes)
                .totalComissoesVendedorProcessadas(totalComissoesVendedorProcessadas)
                .valorTotalComissoesPendentes(valorTotalComissoesPendentes)
                .custoMedioPorFuncionario(custoMedioPorFuncionario)
                .percentualComissoesSobreFolha(percentualComissoesSobreFolha)
                .funcionariosSemFolhaNoMes(funcionariosSemFolhaNoMes)
                .funcionariosComComissaoPendente(funcionariosComComissaoPendente)
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