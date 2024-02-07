package com.github.lomasz.spring.boot.template.application.usecase;

import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.TemplateStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchTemplatesUseCase implements UseCase<SearchTemplatesUseCase.Input, SearchTemplatesUseCase.Output> {

    private final TemplateStorage templateStorage;

    @Override
    public Output execute(Input input) {
        return new Output(templateStorage.search(input.page(), input.size(), input.sortDirection(), input.sortProperty));
    }

    public record Input(int page, int size, SortDirection sortDirection, String sortProperty) implements InputValues {
    }

    public record Output(SearchResult<Template> result) implements OutputValues {
    }
}
