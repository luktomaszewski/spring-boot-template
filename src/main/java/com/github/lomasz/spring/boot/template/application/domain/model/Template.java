package com.github.lomasz.spring.boot.template.application.domain.model;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record Template(
        Long id,
        String name,
        String acronym,
        BigDecimal budget
) { }
