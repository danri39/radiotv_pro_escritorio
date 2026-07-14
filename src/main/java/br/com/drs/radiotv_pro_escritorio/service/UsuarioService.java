package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.LoginResponseDTO;
import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.UsuarioMapper;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setores;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Sistemas;
import br.com.drs.radiotv_pro_escritorio.repository.UsuarioRepository;
import br.com.drs.radiotv_pro_escritorio.util.CriarChaves;
import br.com.drs.radiotv_pro_escritorio.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvar(UsuarioDTO dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegraNegocioException("E-mail já cadastrado no sistema.");
        }

        Usuario novoUsuario = mapper.toEntity(dto);
        String chaveUser = CriarChaves.gerarChaveUsuario();
        String chaveCadastro = CriarChaves.gerarChavePrimeiroAcesso();
        novoUsuario.setChaveUsuario(chaveUser);
        novoUsuario.setChavePrimeiroAcesso(chaveCadastro);
        novoUsuario.setPapeis(Papeis.CONVIDADO);
        novoUsuario.setAcessoEscritorio(false);
        novoUsuario.setSetores(Collections.singletonList(Setores.OUTROS));
        novoUsuario.setAtivo(true);
        novoUsuario.setSenha(null);

        repository.save(novoUsuario);
        emailService.enviarEmailCadastro(novoUsuario.getEmail(), chaveUser, chaveCadastro);

        return mapper.toDTO(novoUsuario);
    }

    public List<UsuarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o ID: " + id));
    }

    public UsuarioDTO atualizar(UsuarioDTO dto, Long id) {
        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o ID: " + id));

        mapper.updateEntityFromDto(dto, usuarioExistente);
        repository.save(usuarioExistente);

        return mapper.toDTO(usuarioExistente);
    }

    public void apagar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Não foi possível excluir. Usuário não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public LoginResponseDTO login(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChave(dto.getChaveUsuario());
        validarCredenciaisLogin(usuario, dto.getSenha());

        usuario.setAcessoSistema(LocalDateTime.now());
        repository.save(usuario);

        return new LoginResponseDTO(jwtUtil.gerarToken(usuario), mapper.toDTO(usuario));
    }

    @Transactional
    public void definirSenhaPrimeiroAcesso(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChavePrimeiroAcesso(dto.getChavePrimeiroAcesso());
        usuario.finalizarPrimeiroAcesso(passwordEncoder.encode(dto.getSenha()));
        repository.save(usuario);
    }

    @Transactional
    public void solicitarTrocaSenha(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChave(dto.getChaveUsuario());
        String chaveTroca = CriarChaves.gerarChaveTrocaSenha();
        usuario.setChaveTrocaSenha(chaveTroca);
        repository.save(usuario);

        emailService.enviarEmailTrocaSenha(usuario.getEmail(), usuario.getChaveTrocaSenha());
    }

    @Transactional
    public void efetivarTrocaSenha(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChaveTrocaSenha(dto.getChaveTrocaSenha());
        usuario.alterarSenha(passwordEncoder.encode(dto.getSenha()));
        repository.save(usuario);
    }

    @Transactional
    public UsuarioDTO cadastrar(UsuarioDTO dto) {
        validarEmailUnico(dto.getEmail());

        Usuario novoUsuario = mapper.toEntity(dto);

        // ==========================================
        // GERAÇÃO DAS CHAVES DE ACESSO (ALFANUMÉRICAS E MAIÚSCULAS)
        // ==========================================

        // Gera Chave de Usuário (4 caracteres alfanuméricos únicos)
        String chaveUsuario;
        do {
            chaveUsuario = CriarChaves.gerarChaveUsuario();
        } while (repository.existsByChaveUsuario(chaveUsuario));

        // Gera Chave de Primeiro Acesso (40 caracteres alfanuméricos)
        String chavePrimeiroAcesso = CriarChaves.gerarChavePrimeiroAcesso();

        // Define os valores gerados
        novoUsuario.setChaveUsuario(chaveUsuario);
        novoUsuario.setChavePrimeiroAcesso(chavePrimeiroAcesso);
        novoUsuario.setPrimeiroAcesso(true);

        // ==========================================
        // OUTROS CAMPOS OBRIGATÓRIOS
        // ==========================================
        novoUsuario.setAcessoEscritorio(false);
        novoUsuario.setAtivo(true);
        novoUsuario.setPapeis(Papeis.CONVIDADO);
        novoUsuario.setSetores(Collections.singletonList(Setores.OUTROS));

        repository.save(novoUsuario);

        emailService.enviarEmailCadastro(
                novoUsuario.getEmail(),
                novoUsuario.getChaveUsuario(),
                novoUsuario.getChavePrimeiroAcesso()
        );

        return mapper.toDTO(novoUsuario);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorId(id);

        // PRESERVA OS CAMPOS QUE NÃO PODEM SER ALTERADOS
        String chaveUsuarioOriginal = usuario.getChaveUsuario();
        String senhaOriginal = usuario.getSenha();

        // O MapStruct atualiza tudo
        mapper.updateEntityFromDto(dto, usuario);

        // RESTAURA OS CAMPOS PROTEGIDOS
        usuario.setChaveUsuario(chaveUsuarioOriginal);
        usuario.setSenha(senhaOriginal);

        // Atualiza senha apenas se veio uma nova no DTO
        atualizarSenhaSeNecessario(dto, usuario);

        return mapper.toDTO(repository.save(usuario));
    }

    @Transactional
    public void deletar(Long id) {
        // Evita a dupla consulta do existsById + deleteById
        repository.delete(buscarUsuarioPorId(id));
    }

    // ==========================================
    // MÉTODOS PRIVADOS (Extraídos para limpar o código)
    // ==========================================

    private Usuario buscarUsuarioPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public Usuario buscarUsuarioPorChave(String chaveUsuario) {
        // Adicione esta verificação
        if (chaveUsuario == null || chaveUsuario.isBlank()) {
            throw new IllegalArgumentException("A chave do usuário não foi fornecida na requisição.");
        }

        System.out.println("DEBUG: Buscando no banco pela chave: [" + chaveUsuario + "]");

        return repository.findByChaveUsuario(chaveUsuario)
                .orElseThrow(() -> {
                    System.out.println("ERRO: Usuário não encontrado para a chave: " + chaveUsuario);
                    return new IllegalArgumentException("Chave de usuário inválida: " + chaveUsuario);
                });
    }

    private Usuario buscarUsuarioPorChavePrimeiroAcesso(String chave) {
        return repository.findByChavePrimeiroAcesso(chave)
                .orElseThrow(() -> new IllegalArgumentException("Chave de primeiro acesso inválida."));
    }

    private Usuario buscarUsuarioPorChaveTrocaSenha(String chave) {
        return repository.findByChaveTrocaSenha(chave)
                .orElseThrow(() -> new IllegalArgumentException("Chave de troca de senha inválida."));
    }

    private void validarCredenciaisLogin(Usuario usuario, String senhaInformada) {
        if (Boolean.FALSE.equals(usuario.getAtivo())) throw new IllegalStateException("Usuário inativo.");
        if (Boolean.TRUE.equals(usuario.getPrimeiroAcesso())) throw new IllegalStateException("Realize o primeiro acesso.");
        if (!passwordEncoder.matches(senhaInformada, usuario.getSenha())) throw new IllegalArgumentException("Senha incorreta.");
    }

    private void validarEmailUnico(String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
    }

    private void atualizarSenhaSeNecessario(UsuarioDTO dto, Usuario usuario) {
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        // Se a senha vier em branco, o MapStruct (com ignore=true) já preservou a senha antiga na entidade!
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O email do usuário não foi fornecido.");
        }

        System.out.println("🔍 DEBUG: Buscando no banco pelo EMAIL: [" + email + "]");

        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ ERRO: Usuário não encontrado para o email: " + email);
                    return new IllegalArgumentException("Email não encontrado: " + email);
                });
    }
}
