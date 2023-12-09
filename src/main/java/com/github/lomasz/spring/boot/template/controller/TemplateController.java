package com.github.lomasz.spring.boot.template.controller;

import com.github.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.github.lomasz.spring.boot.template.model.dto.SearchResult;
import com.github.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.github.lomasz.spring.boot.template.service.TemplateService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
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
public class TemplateController implements TemplateApiDoc {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<SearchResult<TemplateDto>> search(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "order", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sortProperty) {
        return ResponseEntity.ok(templateService.search(page, size, direction, sortProperty));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid NewTemplateDto newDto) {
        Long id = templateService.create(newDto);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/templates/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateDto> getById(@PathVariable("id") Long id) {
        return templateService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
