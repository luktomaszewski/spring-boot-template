package com.github.lomasz.spring.boot.template.application.domain.model;

import java.util.List;
import java.util.function.Function;

public record SearchResult<T>(
        List<T> items,
        Long totalCount,
        Integer page,
        Integer limit,
        Integer pages
) {

    public static <T, R> SearchResult<R> from(SearchResult<T> source, Function<T, R> mapper) {
        List<R> mappedItems = source.items().stream()
                .map(mapper)
                .toList();

        return new SearchResult<>(mappedItems, source.totalCount(), source.page(), source.limit(), source.pages());
    }

}
