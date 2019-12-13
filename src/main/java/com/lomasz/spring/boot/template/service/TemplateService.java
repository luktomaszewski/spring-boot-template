package com.lomasz.spring.boot.template.service;

import com.lomasz.spring.boot.template.mapper.TemplateMapper;
import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.SearchResult;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.model.entity.TemplateEntity;
import com.lomasz.spring.boot.template.repository.TemplateRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CommonsLog
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Autowired
    public TemplateService(TemplateRepository templateRepository, TemplateMapper templateMapper) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
    }

    public SearchResult<TemplateDto> search(int page, int size, Sort.Direction direction, String sortProperty) {
        Page<TemplateEntity> teamPage = templateRepository.findAll(PageRequest.of(page, size, direction, sortProperty));
        List<TemplateDto> items = templateMapper.toDtoList(teamPage.getContent());

        return SearchResult.<TemplateDto>builder()
                .items(items)
                .limit(teamPage.getPageable().getPageSize())
                .page(teamPage.getPageable().getPageNumber())
                .pages(teamPage.getTotalPages())
                .totalCount(teamPage.getTotalElements())
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
