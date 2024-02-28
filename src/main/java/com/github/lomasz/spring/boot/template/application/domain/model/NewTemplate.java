package com.github.lomasz.spring.boot.template.application.domain.model;

import java.math.BigDecimal;

public record NewTemplate(
        String name,
        String acronym,
        BigDecimal budget
) {
}
