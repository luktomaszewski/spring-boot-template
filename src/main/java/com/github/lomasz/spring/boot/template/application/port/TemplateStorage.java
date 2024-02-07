package com.github.lomasz.spring.boot.template.application.port;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import java.util.Optional;

public interface TemplateStorage {

    SearchResult<Template> search(int page, int size, SortDirection sortDirection, String sortProperty);

    Long create(NewTemplate newTemplate);

    Optional<Template> findById(Long id);
}
