package br.com.drs.radiotv_pro_escritorio.util;

import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final SecretKey CHAVE_SECRETA = Keys.hmacShaKeyFor("chavedeSegurançaParaoProgramaRádioTv_App_Pro".getBytes());

    private static final long EXPIRATION_TIME = 86400000; // 24 horas em milissegundos

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail()) // Na v0.12.x, usa-se .subject() em vez de .setSubject()
                .claim("nome", usuario.getNome())
                .claim("chaveUsuario", usuario.getChaveUsuario())
                .claim("papel", usuario.getPapel().toString())
                .claim("setor", usuario.getSetor().stream().map(Enum::toString).toList())
                .claim("acessoEscritorio", usuario.getAcessoEscritorio())
                .claim("funcionarioId", usuario.getFuncionarioId())
                .claim("ativo", usuario.getAtivo())
                .issuedAt(new Date())        // Na v0.12.x, usa-se .issuedAt() em vez de .setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // .expiration() em vez de .setExpiration()
                .signWith(CHAVE_SECRETA)
                .compact();
    }

    public String extrairEmail(String token) {
        try {
            // API MODERNA DO JJWT 0.12.x
            Claims claims = Jwts.parser()
                    .verifyWith(CHAVE_SECRETA)       // Substitui o antigo .setSigningKey()
                    .build()
                    .parseSignedClaims(token)        // Substitui o antigo .parseClaimsJws()
                    .getPayload();                   // Substitui o antigo .getBody()

            return claims.getSubject();
        } catch (Exception e) {
            // Token inválido, expirado ou malformado
            return null;
        }
    }

    public boolean tokenValido(String token) {
        try {
            // API MODERNA DO JJWT 0.12.x
            Claims claims = Jwts.parser()
                    .verifyWith(CHAVE_SECRETA)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verifica se a data de expiração é posterior à data atual
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Qualquer exceção (expirado, assinatura inválida, etc.) torna o token inválido
            return false;
        }
    }
}