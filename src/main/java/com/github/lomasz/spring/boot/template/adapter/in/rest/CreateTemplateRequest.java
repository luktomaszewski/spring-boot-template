package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateTemplateRequest(

        @NotNull
        @Size(max = 255)
        String name,

        @NotNull
        @Size(min = 1, max = 5)
        String acronym,

        @NotNull
        @Min(value = 0L, message = "must be positive")
        BigDecimal budget
) {

    public NewTemplate toDomain() {
        return new NewTemplate(name, acronym, budget);
    }
}
