package com.lomasz.spring.boot.template.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final Contact CONTACT  =
            new Contact("lomasz", "", "lukasz.tomaszewski89@gmail.com");

    @Bean
    public Docket swaggerApi() {
        final ApiInfo apiInfo = new ApiInfoBuilder()
                .title("REST API")
                .description("REST API")
                .contact(CONTACT)
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .ignoredParameterTypes(Pageable.class, PagedResourcesAssembler.class)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo);
    }

}
