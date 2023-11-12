package com.lomasz.spring.boot.template.mapper;

import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.model.entity.TemplateEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TemplateMapper {

    @Mapping(target = "id", ignore = true)
    TemplateEntity toEntity(NewTemplateDto dto);

    TemplateDto toDto(TemplateEntity entity);

    List<TemplateDto> toDtos(List<TemplateEntity> entities);
}
