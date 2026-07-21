package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ComprasDTO;
import br.com.drs.radiotv_pro_escritorio.dto.ItemCompraDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.ComprasMapper;
import br.com.drs.radiotv_pro_escritorio.mapper.ItemCompraMapper;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.ItemCompra;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusCompra;
import br.com.drs.radiotv_pro_escritorio.repository.ComprasRepository;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComprasService {

    private final ComprasRepository comprasRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ComprasMapper comprasMapper;

    // ==========================================
    // 1. CRIAR COMPRA (Usuário do Escritório)
    // ==========================================
    @Transactional
    public ComprasDTO criarCompra(ComprasDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + dto.getFuncionarioId()));

        Compras compra = comprasMapper.toEntity(dto);
        compra.setFuncionario(funcionario);

        compra.setStatusCompra(StatusCompra.PENDENTE);
        compra.setChaveAdministrador(null);
        compra.setJustificativaRecusa(null);
        compra.setDataPagamento(null);
        compra.setAtiva(true);
        compra.setDataCompra(dto.getDataCompra() != null ? dto.getDataCompra() : LocalDate.now());

        // Calcula o valor total geral somando os itens
        BigDecimal total = compra.getItens().stream()
                .map(ItemCompra::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        compra.setValorTotalGeral(total);

        Compras compraSalva = comprasRepository.save(compra);
        return comprasMapper.toDTO(compraSalva);
    }

    // ==========================================
    // 2 a 6. LISTAGENS (mantidos como antes)
    // ==========================================
    @Transactional(readOnly = true)
    public List<ComprasDTO> listarTodas() {
        return comprasMapper.toDTOList(comprasRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ComprasDTO buscarPorId(Long id) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));
        return comprasMapper.toDTO(compra);
    }

    @Transactional(readOnly = true)
    public List<ComprasDTO> listarPorFuncionario(Long funcionarioId) {
        return comprasMapper.toDTOList(
                comprasRepository.findByFuncionario_FuncionarioIdOrderByDataCompraDesc(funcionarioId)
        );
    }

    @Transactional(readOnly = true)
    public List<ComprasDTO> listarPorStatus(StatusCompra status) {
        return comprasMapper.toDTOList(comprasRepository.findByStatusCompra(status));
    }

    @Transactional(readOnly = true)
    public List<ComprasDTO> listarPendentesAprovacao() {
        return comprasMapper.toDTOList(
                comprasRepository.findByStatusCompraAndAtivaTrue(StatusCompra.PENDENTE)
        );
    }

    // ==========================================
    // 7. EDITAR COMPRA (Usuário do Escritório)
    // ==========================================
    @Transactional
    public ComprasDTO editarCompra(Long id, ComprasDTO dto) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));

        if (compra.getStatusCompra() == StatusCompra.PAGA) {
            throw new RuntimeException("Não é possível editar uma compra já paga.");
        }

        if (compra.getStatusCompra() == StatusCompra.RECUSADA) {
            throw new RuntimeException("Compra recusada não pode ser editada. Crie uma nova solicitação.");
        }

        compra.setDataCompra(dto.getDataCompra());

        // Atualiza os itens
        compra.getItens().clear();
        if (dto.getItens() != null) {
            for (ItemCompraDTO itemDto : dto.getItens()) {
                ItemCompra item = ItemCompraMapper.INSTANCE.toEntity(itemDto);
                compra.addItem(item);
            }
        }

        // Recalcula o total
        BigDecimal total = compra.getItens().stream()
                .map(ItemCompra::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        compra.setValorTotalGeral(total);

        // Se estava aprovada, perde a aprovação ao ser editada
        if (compra.getStatusCompra() == StatusCompra.APROVADA) {
            compra.invalidarAprovacao();
        }

        Compras compraSalva = comprasRepository.save(compra);
        return comprasMapper.toDTO(compraSalva);
    }

    // ==========================================
    // 8 a 12. APROVAR, RECUSAR, PAGAR, INATIVAR, REATIVAR
    // (mantidos como antes)
    // ==========================================
    @Transactional
    public ComprasDTO aprovarCompra(Long id) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));

        if (compra.getStatusCompra() != StatusCompra.PENDENTE) {
            throw new RuntimeException("Apenas compras PENDENTES podem ser aprovadas.");
        }

        compra.setChaveAdministrador(UUID.randomUUID().toString());
        compra.setStatusCompra(StatusCompra.APROVADA);
        compra.setJustificativaRecusa(null);

        Compras compraSalva = comprasRepository.save(compra);
        return comprasMapper.toDTO(compraSalva);
    }

    @Transactional
    public ComprasDTO recusarCompra(Long id, String justificativa) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));

        if (compra.getStatusCompra() != StatusCompra.PENDENTE) {
            throw new RuntimeException("Apenas compras PENDENTES podem ser recusadas.");
        }

        if (justificativa == null || justificativa.isBlank()) {
            throw new RuntimeException("É obrigatório informar a justificativa da recusa.");
        }

        compra.setStatusCompra(StatusCompra.RECUSADA);
        compra.setJustificativaRecusa(justificativa);
        compra.setChaveAdministrador(null);

        Compras compraSalva = comprasRepository.save(compra);
        return comprasMapper.toDTO(compraSalva);
    }

    @Transactional
    public ComprasDTO pagarCompra(Long id) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));

        if (compra.getChaveAdministrador() == null || compra.getChaveAdministrador().isBlank()) {
            throw new RuntimeException("BLOQUEIO DE SEGURANÇA: Compra sem autorização do administrador.");
        }

        if (compra.getStatusCompra() != StatusCompra.APROVADA) {
            throw new RuntimeException("Apenas compras APROVADAS podem ser pagas.");
        }

        compra.setStatusCompra(StatusCompra.PAGA);
        compra.setDataPagamento(LocalDate.now());

        Compras compraSalva = comprasRepository.save(compra);
        return comprasMapper.toDTO(compraSalva);
    }

    @Transactional
    public void inativarCompra(Long id) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));

        if (compra.getStatusCompra() == StatusCompra.PAGA) {
            throw new RuntimeException("Não é possível inativar uma compra já paga.");
        }

        compra.setAtiva(false);
        comprasRepository.save(compra);
    }

    @Transactional
    public void reativarCompra(Long id) {
        Compras compra = comprasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));
        compra.setAtiva(true);
        comprasRepository.save(compra);
    }
}