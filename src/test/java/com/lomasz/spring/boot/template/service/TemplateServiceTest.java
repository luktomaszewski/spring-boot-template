package com.lomasz.spring.boot.template.service;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.mapper.TemplateMapper;
import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.SearchResult;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.model.entity.TemplateEntity;
import com.lomasz.spring.boot.template.repository.TemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateMapper templateMapper;

    @InjectMocks
    private TemplateService templateService;

    @Test
    void create() {
        // given
        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        NewTemplateDto dto = new NewTemplateDto(name, acronym, budget);

        TemplateEntity newEntity = new TemplateEntity();
        newEntity.setName(name);
        newEntity.setAcronym(acronym);
        newEntity.setBudget(budget);

        TemplateEntity savedEntity = new TemplateEntity();
        savedEntity.setId(id);
        savedEntity.setName(name);
        savedEntity.setAcronym(acronym);
        savedEntity.setBudget(budget);

        when(templateMapper.toEntity(dto)).thenReturn(newEntity);
        when(templateRepository.save(newEntity)).thenReturn(savedEntity);

        // when
        Long result = templateService.create(dto);

        // then
        assertThat(result).isEqualTo(id);

        verify(templateMapper, times(1)).toEntity(dto);
        verify(templateRepository, times(1)).save(newEntity);
    }

    @Test
    void findByIdWhenEntityDoesntExistsShouldReturnOptionalEmpty() {
        // given
        Long id = 1L;

        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<TemplateDto> result = templateService.findById(id);

        // then
        assertFalse(result.isPresent());

        verify(templateRepository, times(1)).findById(id);
        verify(templateMapper, never()).toDto(any(TemplateEntity.class));
    }

    @Test
    void findByIdWhenEntityExistsShouldReturnOptionalTeamDto() {
        // given
        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        TemplateEntity entity = new TemplateEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setAcronym(acronym);
        entity.setBudget(budget);

        TemplateDto dto = new TemplateDto();
        dto.setId(id);
        dto.setName(name);

        when(templateRepository.findById(id)).thenReturn(Optional.of(entity));
        when(templateMapper.toDto(entity)).thenReturn(dto);

        // when
        Optional<TemplateDto> result = templateService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertThat(result.get()).isEqualTo(dto);

        verify(templateRepository, times(1)).findById(id);
        verify(templateMapper, times(1)).toDto(entity);
    }

    @Test
    void search() {
        // given
        int page = 0;
        int limit = 5;
        Sort.Direction sortDirection = Sort.Direction.DESC;
        String sortBy = "sort";

        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        TemplateEntity entity = new TemplateEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setAcronym(acronym);
        entity.setBudget(budget);

        List<TemplateEntity> entityList = Collections.singletonList(entity);

        TemplateDto dto = new TemplateDto();
        dto.setId(id);
        dto.setName(name);

        List<TemplateDto> dtoList = Collections.singletonList(dto);

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
        when(templateMapper.toDtoList(entityList)).thenReturn(dtoList);

        // when
        SearchResult<TemplateDto> result = templateService.search(page, limit, sortDirection, sortBy);

        // then
        assertThat(result.getItems()).isEqualTo(dtoList);
        assertThat(result.getLimit()).isEqualTo(limit);
        assertThat(result.getPage()).isEqualTo(page);
        assertThat(result.getPages()).isEqualTo(totalPages);
        assertThat(result.getTotalCount()).isEqualTo(totalElements);

        verify(templateRepository, times(1)).findAll(any(Pageable.class));
        verify(templateMapper, times(1)).toDtoList(entityList);
    }

    @Test
    void searchWhenWrongSortValueShouldReturnBusinessException() {
        // given
        int page = 0;
        int limit = 5;
        Sort.Direction sortDirection = Sort.Direction.DESC;
        String sortBy = "wrongField";

        when(templateRepository.findAll(any(Pageable.class))).thenThrow(PropertyReferenceException.class);

        // when
        // then
        assertThrows(BusinessException.class, () -> templateService.search(page, limit, sortDirection, sortBy));
        verify(templateRepository, times(1)).findAll(any(Pageable.class));
        verify(templateMapper, never()).toDtoList(anyList());
    }

}
