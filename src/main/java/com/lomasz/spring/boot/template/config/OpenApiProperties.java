package com.lomasz.spring.boot.template.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "info.app")
public class OpenApiProperties {

    private String name;
    private String version;

}
