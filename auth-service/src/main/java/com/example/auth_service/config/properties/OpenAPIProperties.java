package com.example.auth_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "openapi")
public class OpenAPIProperties {

    private String devUrl;
    private String prodUrl;
    private String email;
    private String name;
    private String url;

}
