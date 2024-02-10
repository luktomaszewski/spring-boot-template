package com.github.lomasz.spring.boot.template.application.port;

import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;

public interface SearchTemplatePort {

    SearchResult<Template> search(int page, int size, SortDirection sortDirection, String sortProperty);

}
