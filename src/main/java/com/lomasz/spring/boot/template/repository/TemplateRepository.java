package com.lomasz.spring.boot.template.repository;

import com.lomasz.spring.boot.template.model.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {}
