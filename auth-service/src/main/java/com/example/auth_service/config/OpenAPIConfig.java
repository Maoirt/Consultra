package com.example.auth_service.config;


import com.example.auth_service.config.properties.OpenAPIProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final OpenAPIProperties properties;

    @Bean
    public OpenAPI myOpenAPI() {

        Server devServer = new Server();
        devServer.setUrl(properties.getDevUrl());
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(properties.getProdUrl());
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail(properties.getEmail());
        contact.setName(properties.getName());
        contact.setUrl(properties.getUrl());

        Info info = new Info()
                .title("User Profiles API")
                .version("1.0")
                .contact(contact)
                .description("Этот API предоставляет конечные точки сервиса аутентификации.");

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}

