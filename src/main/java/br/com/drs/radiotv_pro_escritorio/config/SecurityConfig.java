package br.com.drs.radiotv_pro_escritorio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Usa o seu bean de CORS
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {

                    // 1. Rotas de Auth
                    req.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/v1/usuarios/public/login/**").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/v1/usuarios/public/cadastrar/**").permitAll();
                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // 2. Rotas da API (mantidas como você configurou)
                    req.requestMatchers("/api/v1/usuarios/**").permitAll();
                    req.requestMatchers("/api/v1/funcionarios/**").permitAll();
                    req.requestMatchers("/api/v1/familias/**").permitAll();
                    req.requestMatchers("/api/v1/filhos/**").permitAll();
                    req.requestMatchers("/api/v1/agencias/**").permitAll();
                    req.requestMatchers("/api/v1/vendedor/**").permitAll();
                    req.requestMatchers("/api/v1/clientes/**").permitAll();
                    req.requestMatchers("/api/v1/ramoAtividade/**").permitAll();
                    req.requestMatchers("/api/v1/beneficios/**").permitAll();
                    req.requestMatchers("/api/v1/contaBancaria/**").permitAll();
                    req.requestMatchers("/api/v1/programas/**").permitAll();
                    req.requestMatchers("/api/v1/contratos/**").permitAll();
                    req.requestMatchers("/api/v1/compras/**").permitAll();
                    req.requestMatchers("/api/v1/contratos-pagamento/**").permitAll();
                    req.requestMatchers("/api/v1/pagamentos/**").permitAll();
                    req.requestMatchers("/api/v1/comissoes-vendedor/**").permitAll();
                    req.requestMatchers("/api/v1/administrador/**").permitAll();
                    req.requestMatchers("/api/v1/gerencial/**").permitAll();
                    req.requestMatchers("/api/v1/feriado/**").permitAll();

                    // 3. NOVO: Liberar acesso ao Swagger UI e documentação
                    req.requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**"
                    ).permitAll();

                    // 4. Qualquer outra rota requer autenticação
                    req.anyRequest().authenticated();
                })
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}