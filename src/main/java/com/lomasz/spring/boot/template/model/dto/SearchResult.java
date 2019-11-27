package com.lomasz.spring.boot.template.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SearchResult<T> implements Serializable {

    private static final long serialVersionUID = 8638435681375964215L;

    private List<T> items;
    private Long totalCount;
    private Integer page;
    private Integer limit;
    private Integer pages;

}
