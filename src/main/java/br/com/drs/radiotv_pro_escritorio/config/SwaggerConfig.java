package br.com.drs.radiotv_pro_escritorio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API RadioTV Pro Escritório")
                        .version("1.0")
                        .description("Documentação dos endpoints do sistema de gestão comercial, financeiro e RH."));
    }
}