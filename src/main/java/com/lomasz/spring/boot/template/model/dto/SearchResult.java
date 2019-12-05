package com.lomasz.spring.boot.template.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResult<T> {

    private List<T> items;

    private Long totalCount;

    private Integer page;

    private Integer limit;

    private Integer pages;

}
