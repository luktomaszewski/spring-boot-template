package com.github.lomasz.spring.boot.template.application.domain.model;

import java.util.List;
import lombok.Builder;

@Builder
public record SearchResult<T>(
        List<T> items,
        Long totalCount,
        Integer page,
        Integer limit,
        Integer pages
) {
}
