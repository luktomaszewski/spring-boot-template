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
import java.math.BigDecimal;
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
        BigDecimal budget = BigDecimal.valueOf(182005000);

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
    @DisplayName("operation: findById, should: throw NotFoundException, when: does not exist")
    void findByIdWhenEntityDoesNotExistsShouldReturnOptionalEmpty() {
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
        TemplateEntity entity = TemplateEntity.builder()
                .id(1L)
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(182005000))
                .build();

        when(templateRepository.findById(1L)).thenReturn(Optional.of(entity));

        // when
        Template result = sut.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.acronym()).isEqualTo("JD");
        assertThat(result.budget()).isEqualTo(BigDecimal.valueOf(182005000));
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
        BigDecimal budget = BigDecimal.valueOf(182005000);

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
        assertThat(result.items()).contains(dto);
        assertThat(result.limit()).isEqualTo(limit);
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.pages()).isEqualTo(totalPages);
        assertThat(result.totalCount()).isEqualTo(totalElements);
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
