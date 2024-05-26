package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateTemplateRequest(

        @NotNull
        @Size(max = 255)
        @Schema(example = "John Doe")
        String name,

        @NotNull
        @Size(min = 1, max = 5)
        @Schema(example = "JD")
        String acronym,

        @NotNull
        @Min(value = 0L, message = "must be positive")
        @Schema(example = "1000.00")
        BigDecimal budget
) {

    public Template toDomain() {
        return new Template(null, name, acronym, budget);
    }
}
