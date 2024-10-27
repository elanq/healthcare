package com.fastcampus.healthcare.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .components(new Components().addSecuritySchemes("Bearer",
            new SecurityScheme().type(Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT"))
        )
        .info(new Info()
            .title("Healthcare API")
            .version("1.0")
            .description("Aplikasi healthcare")
        );
  }
}
