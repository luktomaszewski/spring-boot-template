package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import java.math.BigDecimal;

public record TemplateResponse(
        Long id,
        String name,
        String acronym,
        BigDecimal budget
) {

    public static TemplateResponse fromDomain(Template template) {
        return new TemplateResponse(template.id(), template.name(), template.acronym(), template.budget());
    }
}
