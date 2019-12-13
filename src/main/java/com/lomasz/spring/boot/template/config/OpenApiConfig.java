package com.lomasz.spring.boot.template.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${info.app.name}")
    private String name;

    @Value("${info.app.version}")
    private String version;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(info());
    }

    private Info info() {
        return new Info()
                .title(name)
                .version(version)
                .description("Spring Boot Template REST API")
                .contact(contact());
    }

    private Contact contact() {
        return new Contact()
                .name("Lomasz")
                .url("https://github.com/lomasz")
                .email("lukasz.tomaszewski89@gmail.com");
    }

}
