package com.lomasz.spring.boot.template.service;

import com.lomasz.spring.boot.template.exception.BusinessException;
import com.lomasz.spring.boot.template.mapper.TemplateMapper;
import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.SearchResult;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.model.entity.TemplateEntity;
import com.lomasz.spring.boot.template.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CommonsLog
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    public SearchResult<TemplateDto> search(int page, int size, Sort.Direction direction, String sortProperty) {
        Page<TemplateEntity> resultPage;
        try {
            resultPage = templateRepository.findAll(PageRequest.of(page, size, direction, sortProperty));
        } catch (PropertyReferenceException e){
            throw new BusinessException("No sort property found: " + e.getPropertyName());
        }
        List<TemplateDto> items = templateMapper.toDtoList(resultPage.getContent());

        return SearchResult.<TemplateDto>builder()
                .items(items)
                .limit(resultPage.getPageable().getPageSize())
                .page(resultPage.getPageable().getPageNumber())
                .pages(resultPage.getTotalPages())
                .totalCount(resultPage.getTotalElements())
                .build();
    }

    public Long create(NewTemplateDto newDto) {
        log.info("Saving new object: " + newDto);
        TemplateEntity savedEntity = templateRepository.save(templateMapper.toEntity(newDto));
        log.info("New entity saved in the database successfully: " + savedEntity);
        return savedEntity.getId();
    }

    public Optional<TemplateDto> findById(Long id) {
        return templateRepository.findById(id)
                .map(templateMapper::toDto);
    }

}
