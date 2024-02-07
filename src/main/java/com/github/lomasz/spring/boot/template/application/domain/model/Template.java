package com.github.lomasz.spring.boot.template.application.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Template {

    private Long id;
    private String name;
    private String acronym;
    private Long budget;
}
