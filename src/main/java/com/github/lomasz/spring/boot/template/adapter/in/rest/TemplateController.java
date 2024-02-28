package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.usecase.AddTemplateUseCase;
import com.github.lomasz.spring.boot.template.application.usecase.GetTemplateUseCase;
import com.github.lomasz.spring.boot.template.application.usecase.SearchTemplatesUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
class TemplateController implements TemplateApiDoc {

    private final SearchTemplatesUseCase searchTemplatesUseCase;
    private final AddTemplateUseCase addTemplateUseCase;
    private final GetTemplateUseCase getTemplateUseCase;

    @GetMapping
    public ResponseEntity<SearchResult<TemplateResponse>> search(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "order", required = false, defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sortProperty) {
        SearchResult<Template> result = searchTemplatesUseCase.execute(new SearchTemplatesUseCase.Input(page, size, sortDirection, sortProperty)).result();
        List<TemplateResponse> items = result.items().stream()
                .map(TemplateResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(new SearchResult<>(items, result.totalCount(), result.page(), result.limit(), result.pages()));
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody @Valid CreateTemplateRequest request) {
        Long id = addTemplateUseCase.execute(new AddTemplateUseCase.Input(request.toDomain())).id();
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/templates/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(TemplateResponse.fromDomain(getTemplateUseCase.execute(new GetTemplateUseCase.Input(id)).template()));
    }
}
