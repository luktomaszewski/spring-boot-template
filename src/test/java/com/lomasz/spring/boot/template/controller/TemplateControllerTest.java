package com.lomasz.spring.boot.template.controller;

import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.SearchResult;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TemplateController sut;

    @Test
    void addShouldReturnNewIdInLocationHeaderAndHttpStatusCreated() {
        // given
        Long id = 1L;
        String name = "John Doe";
        String acronym = "JD";
        NewTemplateDto newTemplateDto = new NewTemplateDto(name, acronym, 182005000L);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(templateService.create(newTemplateDto)).thenReturn(id);

        // when
        ResponseEntity<Void> result = sut.add(newTemplateDto);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(result.getHeaders().get("Location")).get(0)).isEqualTo("/api/" + id);

        verify(templateService, times(1)).create(newTemplateDto);
    }

    @Test
    void search() {
        // given
        int page = 0;
        int limit = 5;
        String sortName = "name";
        Sort.Direction sortOrder = Sort.Direction.DESC;

        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(1L);
        templateDto.setName("name");

        SearchResult<TemplateDto> searchResult = SearchResult.<TemplateDto>builder()
                .items(Collections.singletonList(templateDto))
                .limit(limit)
                .page(page)
                .pages(1)
                .totalCount(1L)
                .build();

        when(templateService.search(page, limit, sortOrder, sortName)).thenReturn(searchResult);

        // when
        ResponseEntity<SearchResult<TemplateDto>> result = sut.search(page, limit, sortOrder, sortName);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(templateService, times(1)).search(page, limit, sortOrder, sortName);
    }

    @Test
    void getByIdWhenDoesntExistTeamDoesntExistShouldReturnHttpStatusNotFound() {
        // given
        Long id = 1L;

        when(templateService.findById(id)).thenReturn(Optional.empty());

        // when
        ResponseEntity<TemplateDto> result = sut.getById(id);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(templateService, times(1)).findById(id);
    }

    @Test
    void getByIdWhenTeamExistsShouldReturnTemplateDtoWithHttpStatusOk() {
        // given
        Long id = 1L;

        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(id);
        templateDto.setName("name");
        templateDto.setAcronym("NA");
        templateDto.setBudget(182005000L);

        when(templateService.findById(id)).thenReturn(Optional.of(templateDto));

        // when
        ResponseEntity<TemplateDto> result = sut.getById(id);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(templateDto);

        verify(templateService, times(1)).findById(id);
    }
}
