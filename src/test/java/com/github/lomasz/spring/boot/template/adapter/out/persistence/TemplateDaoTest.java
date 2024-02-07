package com.github.lomasz.spring.boot.template.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.lomasz.spring.boot.template.application.domain.exception.BusinessException;
import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;

@ExtendWith(MockitoExtension.class)
class TemplateDaoTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateDao sut;

    @Test
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
    void findByIdWhenEntityDoesntExistsShouldReturnOptionalEmpty() {
        // given
        Long id = 1L;

        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Template> result = sut.findById(id);

        // then
        assertFalse(result.isPresent());

        verify(templateRepository).findById(id);
    }

    @Test
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
        Optional<Template> result = sut.findById(id);

        // then
        assertTrue(result.isPresent());
        assertThat(result.get()).isEqualTo(dto);

        verify(templateRepository).findById(id);
    }

    @Test
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

        verify(templateRepository).findAll(any(Pageable.class));
    }

    @Test
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
        verify(templateRepository).findAll(any(Pageable.class));
    }
}
