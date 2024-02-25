package com.github.lomasz.spring.boot.template.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.lomasz.spring.boot.template.application.domain.exception.BusinessException;
import com.github.lomasz.spring.boot.template.application.domain.exception.NotFoundException;
import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;

@ExtendWith(MockitoExtension.class)
class TemplatePersistenceAdapterTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplatePersistenceAdapter sut;

    @Test
    @DisplayName("operation: create, should: return id, when: new template created")
    void create() {
        // given
        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        NewTemplate dto = new NewTemplate(name, acronym, budget);

        TemplateEntity savedEntity = TemplateEntity.builder()
                .id(id)
                .name(name)
                .acronym(acronym)
                .budget(budget)
                .build();

        when(templateRepository.save(any())).thenReturn(savedEntity);

        // when
        Long result = sut.create(dto);

        // then
        assertThat(result).isEqualTo(id);
    }

    @Test
    @DisplayName("operation: findById, should: throw NotFoundException, when: doesn't exist")
    void findByIdWhenEntityDoesntExistsShouldReturnOptionalEmpty() {
        // given
        Long id = 1L;

        // when
        // then
        assertThrows(NotFoundException.class, () -> sut.findById(id));
    }

    @Test
    @DisplayName("operation: findById, should: return template, when: exists")
    void findByIdWhenEntityExistsShouldReturnOptionalTeamDto() {
        // given
        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        TemplateEntity entity = TemplateEntity.builder()
                .id(id)
                .name(name)
                .acronym(acronym)
                .budget(budget)
                .build();

        Template dto = Template.builder()
                .id(id)
                .name(name)
                .acronym(acronym)
                .budget(budget)
                .build();

        when(templateRepository.findById(id)).thenReturn(Optional.of(entity));

        // when
        Template result = sut.findById(id);

        // then
        assertThat(result)
                .isNotNull()
                .isEqualTo(dto);
    }

    @Test
    @DisplayName("operation: search, should: return search result")
    void search() {
        // given
        int page = 0;
        int limit = 5;
        SortDirection sortDirection = SortDirection.DESC;
        String sortBy = "sort";

        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        TemplateEntity entity = TemplateEntity.builder()
                .id(id)
                .name(name)
                .acronym(acronym)
                .budget(budget)
                .build();

        List<TemplateEntity> entityList = Collections.singletonList(entity);

        Template dto = Template.builder()
                .id(id)
                .name(name)
                .acronym(acronym)
                .budget(budget)
                .build();

        long totalElements = 1L;
        int totalPages = 1;

        Pageable pageable = mock(Pageable.class);
        when(pageable.getPageSize()).thenReturn(limit);
        when(pageable.getPageNumber()).thenReturn(page);

        Page<TemplateEntity> templatePage = mock(Page.class);
        when(templatePage.getTotalPages()).thenReturn(totalPages);
        when(templatePage.getTotalElements()).thenReturn(totalElements);
        when(templatePage.getPageable()).thenReturn(pageable);
        when(templatePage.getContent()).thenReturn(entityList);

        when(templateRepository.findAll(any(Pageable.class))).thenReturn(templatePage);

        // when
        SearchResult<Template> result = sut.search(page, limit, sortDirection, sortBy);

        // then
        assertThat(result.getItems()).contains(dto);
        assertThat(result.getLimit()).isEqualTo(limit);
        assertThat(result.getPage()).isEqualTo(page);
        assertThat(result.getPages()).isEqualTo(totalPages);
        assertThat(result.getTotalCount()).isEqualTo(totalElements);
    }

    @Test
    @DisplayName("operation: search, should: throw BusinessException: when: wrong sort field value")
    void searchWhenWrongSortValueShouldReturnBusinessException() {
        // given
        int page = 0;
        int limit = 5;
        SortDirection sortDirection = SortDirection.DESC;
        String sortBy = "wrongField";

        when(templateRepository.findAll(any(Pageable.class))).thenThrow(PropertyReferenceException.class);

        // when
        // then
        assertThrows(BusinessException.class, () -> sut.search(page, limit, sortDirection, sortBy));
    }
}
