package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.LoginResponseDTO;
import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.UsuarioMapper;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Papeis;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Setor;
import br.com.drs.radiotv_pro_escritorio.repository.UsuarioRepository;
import br.com.drs.radiotv_pro_escritorio.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // ==========================================
    // CONSULTAS
    // ==========================================

    public List<UsuarioDTO> buscarTodos() {
        // Dica: Se o seu @Mapper tiver o método toDTO(List<Usuario>), use: usuarioMapper.toDTO(usuarioRepository.findAll())
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        return usuarioMapper.toDTO(buscarUsuarioPorId(id));
    }

    // ==========================================
    // AUTENTICAÇÃO E SENHAS
    // ==========================================

    @Transactional
    public LoginResponseDTO login(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChave(dto.getChaveUsuario());
        validarCredenciaisLogin(usuario, dto.getSenha());

        usuario.registrarAcesso(); // Clean Code: A entidade cuida do seu próprio estado
        usuarioRepository.save(usuario);

        return new LoginResponseDTO(jwtUtil.gerarToken(usuario), usuarioMapper.toDTO(usuario));
    }

    @Transactional
    public void definirSenhaPrimeiroAcesso(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChavePrimeiroAcesso(dto.getChavePrimeiroAcesso());
        usuario.finalizarPrimeiroAcesso(passwordEncoder.encode(dto.getSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void solicitarTrocaSenha(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChave(dto.getChaveUsuario());
        usuario.gerarNovaChaveTrocaSenha();
        usuarioRepository.save(usuario);

        emailService.enviarEmailTrocaSenha(usuario.getEmail(), usuario.getChaveTrocaSenha());
    }

    @Transactional
    public void efetivarTrocaSenha(UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorChaveTrocaSenha(dto.getChaveTrocaSenha());
        usuario.alterarSenha(passwordEncoder.encode(dto.getSenha()));
        usuarioRepository.save(usuario);
    }

    // ==========================================
    // CADASTRO E ATUALIZAÇÃO
    // ==========================================

    @Transactional
    public UsuarioDTO cadastrar(UsuarioDTO dto) {
        validarEmailUnico(dto.getEmail());

        Usuario novoUsuario = usuarioMapper.paraEntity(dto);

        // ==========================================
        // GERAÇÃO DAS CHAVES DE ACESSO (ALFANUMÉRICAS E MAIÚSCULAS)
        // ==========================================

        // Gera Chave de Usuário (4 caracteres alfanuméricos únicos)
        String chaveUsuario;
        do {
            chaveUsuario = gerarChaveAlfanumerica(4);
        } while (usuarioRepository.existsByChaveUsuario(chaveUsuario));

        // Gera Chave de Primeiro Acesso (40 caracteres alfanuméricos)
        String chavePrimeiroAcesso = gerarChaveAlfanumerica(40);

        // Define os valores gerados
        novoUsuario.setChaveUsuario(chaveUsuario);
        novoUsuario.setChavePrimeiroAcesso(chavePrimeiroAcesso);
        novoUsuario.setPrimeiroAcesso(true);

        // ==========================================
        // OUTROS CAMPOS OBRIGATÓRIOS
        // ==========================================
        novoUsuario.setAcessoEscritorio(false);
        novoUsuario.setAtivo(true);
        novoUsuario.setPapel(Papeis.CONVIDADO);
        novoUsuario.setSetor(Collections.singletonList(Setor.OUTROS));

        usuarioRepository.save(novoUsuario);

        emailService.enviarEmailCadastro(
                novoUsuario.getEmail(),
                novoUsuario.getChaveUsuario(),
                novoUsuario.getChavePrimeiroAcesso()
        );

        return usuarioMapper.toDTO(novoUsuario);
    }

    private String gerarChaveAlfanumerica(int tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder chave = new StringBuilder(tamanho);
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < tamanho; i++) {
            int indice = random.nextInt(caracteres.length());
            chave.append(caracteres.charAt(indice));
        }

        return chave.toString(); // Já retorna em maiúsculas
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscarUsuarioPorId(id);

        // PRESERVA OS CAMPOS QUE NÃO PODEM SER ALTERADOS
        String chaveUsuarioOriginal = usuario.getChaveUsuario();
        String senhaOriginal = usuario.getSenha();

        // O MapStruct atualiza tudo
        usuarioMapper.updateEntityFromDto(dto, usuario);

        // RESTAURA OS CAMPOS PROTEGIDOS
        usuario.setChaveUsuario(chaveUsuarioOriginal);
        usuario.setSenha(senhaOriginal);

        // Atualiza senha apenas se veio uma nova no DTO
        atualizarSenhaSeNecessario(dto, usuario);

        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Long id) {
        // Evita a dupla consulta do existsById + deleteById
        usuarioRepository.delete(buscarUsuarioPorId(id));
    }

    // ==========================================
    // MÉTODOS PRIVADOS (Extraídos para limpar o código)
    // ==========================================

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public Usuario buscarUsuarioPorChave(String chaveUsuario) {
        // Adicione esta verificação
        if (chaveUsuario == null || chaveUsuario.isBlank()) {
            throw new IllegalArgumentException("A chave do usuário não foi fornecida na requisição.");
        }

        System.out.println("DEBUG: Buscando no banco pela chave: [" + chaveUsuario + "]");

        return usuarioRepository.findByChaveUsuario(chaveUsuario)
                .orElseThrow(() -> {
                    System.out.println("ERRO: Usuário não encontrado para a chave: " + chaveUsuario);
                    return new IllegalArgumentException("Chave de usuário inválida: " + chaveUsuario);
                });
    }

    private Usuario buscarUsuarioPorChavePrimeiroAcesso(String chave) {
        return usuarioRepository.findByChavePrimeiroAcesso(chave)
                .orElseThrow(() -> new IllegalArgumentException("Chave de primeiro acesso inválida."));
    }

    private Usuario buscarUsuarioPorChaveTrocaSenha(String chave) {
        return usuarioRepository.findByChaveTrocaSenha(chave)
                .orElseThrow(() -> new IllegalArgumentException("Chave de troca de senha inválida."));
    }

    private void validarCredenciaisLogin(Usuario usuario, String senhaInformada) {
        if (Boolean.FALSE.equals(usuario.getAtivo())) throw new IllegalStateException("Usuário inativo.");
        if (Boolean.TRUE.equals(usuario.getPrimeiroAcesso())) throw new IllegalStateException("Realize o primeiro acesso.");
        if (!passwordEncoder.matches(senhaInformada, usuario.getSenha())) throw new IllegalArgumentException("Senha incorreta.");
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
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

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ ERRO: Usuário não encontrado para o email: " + email);
                    return new IllegalArgumentException("Email não encontrado: " + email);
                });
    }
}