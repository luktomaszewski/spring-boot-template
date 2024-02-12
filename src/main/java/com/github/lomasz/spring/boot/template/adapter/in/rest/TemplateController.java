package com.github.lomasz.spring.boot.template.adapter.in.rest;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.usecase.AddTemplateUseCase;
import com.github.lomasz.spring.boot.template.application.usecase.GetTemplateUseCase;
import com.github.lomasz.spring.boot.template.application.usecase.SearchTemplatesUseCase;
import jakarta.validation.Valid;
import java.net.URI;
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
    public ResponseEntity<SearchResult<Template>> search(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "order", required = false, defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sortProperty) {
        return ResponseEntity.ok(searchTemplatesUseCase.execute(new SearchTemplatesUseCase.Input(page, size, sortDirection, sortProperty)).result());
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody @Valid NewTemplate newDto) {
        Long id = addTemplateUseCase.execute(new AddTemplateUseCase.Input(newDto)).id();
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/templates/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getById(@PathVariable("id") Long id) {
        return getTemplateUseCase.execute(new GetTemplateUseCase.Input(id)).template()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
