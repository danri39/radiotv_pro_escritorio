package br.com.drs.radiotv_pro_escritorio.util;

import br.com.drs.radiotv_pro_escritorio.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Gerar uma chave fixa ou injetar via @Value é melhor para não invalidar tokens ao reiniciar
    private static final Key CHAVE_SECRETA = Keys.hmacShaKeyFor("chavedeSegurançaParaoProgramaRádioTv_Pro".getBytes());
    private static final long EXPIRATION_TIME = 86400000; // 24 horas

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("nome", usuario.getPrimeiroNome())
                .claim("chaveUsuario", usuario.getChaveUsuario())
                .claim("papel", usuario.getPapeis().toString())
                .claim("setores", usuario.getSetores().stream().map(Enum::toString).toList())
                .claim("acessoEscritorio", usuario.getAcessoEscritorio())
                .claim("funcionarioId", usuario.getFuncionarioId())
                .claim("ativo", usuario.getAtivo())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // AQUI ESTAVA O ERRO: Use a variável CHAVE_SECRETA que você definiu
                .signWith(CHAVE_SECRETA, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(CHAVE_SECRETA)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean tokenValido(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(CHAVE_SECRETA)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}