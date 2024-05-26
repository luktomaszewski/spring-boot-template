package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

record TemplateResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "John Doe")
        String name,

        @Schema(example = "JD")
        String acronym,

        @Schema(example = "1000.00")
        BigDecimal budget
) {

    public static TemplateResponse fromDomain(Template template) {
        return new TemplateResponse(template.id(), template.name(), template.acronym(), template.budget());
    }
}
