package com.hotel.member_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Member Service API")
                        .description("Gestion des membres et de leurs abonnements (BASIC, PRO, ENTERPRISE)")
                        .version("1.0.0"));
    }
}
