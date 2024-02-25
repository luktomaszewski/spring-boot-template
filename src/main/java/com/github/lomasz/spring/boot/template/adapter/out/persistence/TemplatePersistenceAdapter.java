package com.github.lomasz.spring.boot.template.adapter.out.persistence;

import com.github.lomasz.spring.boot.template.application.domain.exception.NotFoundException;
import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.AddTemplatePort;
import com.github.lomasz.spring.boot.template.application.port.GetTemplatePort;
import com.github.lomasz.spring.boot.template.application.port.SearchTemplatePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplatePersistenceAdapter implements AddTemplatePort, SearchTemplatePort, GetTemplatePort {

    private final TemplateRepository templateRepository;

    @Override
    public SearchResult<Template> search(int page, int size, SortDirection sortDirection, String sortProperty) {
        Page<TemplateEntity> resultPage;
        try {
            resultPage = templateRepository.findAll(PageRequest.of(page, size, mapDirection(sortDirection), sortProperty));
        } catch (PropertyReferenceException e) {
            throw new NoSortPropertyException("No sort property found: " + e.getPropertyName());
        }
        List<Template> items = resultPage.getContent().stream()
                .map(TemplateEntity::toDomain)
                .toList();

        return SearchResult.<Template>builder()
                .items(items)
                .limit(resultPage.getPageable().getPageSize())
                .page(resultPage.getPageable().getPageNumber())
                .pages(resultPage.getTotalPages())
                .totalCount(resultPage.getTotalElements())
                .build();
    }

    private Sort.Direction mapDirection(SortDirection sortDirection) {
        return switch (sortDirection) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };
    }

    @Override
    public Long create(NewTemplate newTemplate) {
        log.info("Saving new object: {}", newTemplate);
        TemplateEntity savedEntity = templateRepository.save(TemplateEntity.fromDomain(newTemplate));
        log.info("New entity saved in the database successfully: {}", savedEntity);
        return savedEntity.getId();
    }

    @Override
    public Template findById(Long id) {
        return templateRepository.findById(id)
                .map(TemplateEntity::toDomain)
                .orElseThrow(() -> new NotFoundException("Template with id=%s not found".formatted(id)));
    }
}
