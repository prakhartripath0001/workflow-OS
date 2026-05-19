// =============================================================================
// OpenAPI / Swagger Configuration
// Access: http://localhost:8080/swagger-ui.html
// JSON:    http://localhost:8080/v3/api-docs
// =============================================================================
package com.workflowos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI slashAIOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SlashAI API")
                        .description("""
                                Production-grade REST API for SlashAI — an AI-powered desktop workspace.
                                
                                **Authentication**: Use the Authorize button to provide your JWT Bearer token.
                                Obtain a token via `POST /api/v1/auth/login` or OAuth2 login.
                                
                                **Base URL**: All endpoints are prefixed with `/api/v1`
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Prakhar Tripathi")
                                .email("contact@slashai.app")
                                .url("https://github.com/praakhartripathi/workflow-OS"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Development"),
                        new Server().url("https://api.slashai.app").description("Production")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT access token (without 'Bearer ' prefix)")));
    }
}
