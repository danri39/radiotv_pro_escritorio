package br.com.drs.radiotv_pro_escritorio.config;

import br.com.drs.radiotv_pro_escritorio.repository.UsuarioRepository;
import br.com.drs.radiotv_pro_escritorio.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    // ✅ USA SEU JwtUtil AO INVÉS DE TokenService
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null && jwtUtil.tokenValido(tokenJWT)) {
            var email = jwtUtil.extrairEmail(tokenJWT);

            if (email != null) {
                // ✅ Usa o map/isPresent para tratar sem lançar exceção
                usuarioRepository.findByEmail(email).ifPresent(usuario -> {

                    var authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name())
                    );

                    var authentication = new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ Usuário autenticado: " + usuario.getNome());
                });
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}