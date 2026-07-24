package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.LoginResponseDTO;
import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.UsuarioMapper;
import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import br.com.drs.radiotv_pro_escritorio.service.UsuarioService;
import br.com.drs.radiotv_pro_escritorio.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final JwtUtil jwtUtil;

    /**
     * Extrai o email do usuário autenticado diretamente do token JWT
     */
    private String extrairEmailDoToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT não encontrado ou inválido");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extrairEmail(token);

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Não foi possível extrair o email do token");
        }

        return email;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> buscarTodos() {
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/public/cadastrar")
    public ResponseEntity<String> cadastrar(@RequestBody UsuarioDTO dto) {
        usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Cadastro realizado! Verifique seu e-mail para obter suas chaves de acesso.");
    }

    @PostMapping("/public/primeiro-acesso")
    public ResponseEntity<String> primeiroAcesso(@RequestBody UsuarioDTO dto) {
        usuarioService.definirSenhaPrimeiroAcesso(dto);
        return ResponseEntity.ok("Senha criada com sucesso! Redirecionando para o login.");
    }

    @PostMapping("/public/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    @PostMapping("/public/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestBody UsuarioDTO dto) {
        usuarioService.solicitarTrocaSenha(dto);
        return ResponseEntity.ok("Se a chave for válida, um e-mail com as instruções foi enviado.");
    }

    @PostMapping("/public/trocar-senha")
    public ResponseEntity<String> trocarSenha(@RequestBody UsuarioDTO dto) {
        usuarioService.efetivarTrocaSenha(dto);
        return ResponseEntity.ok("Senha alterada com sucesso! Redirecionando para o login.");
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> buscarUsuarioAtual(HttpServletRequest request) {
        String emailUsuario = extrairEmailDoToken(request);
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        return ResponseEntity.ok(usuarioMapper.toDTO(usuario));  // ← USAR O MAPPER MANUAL
    }
}