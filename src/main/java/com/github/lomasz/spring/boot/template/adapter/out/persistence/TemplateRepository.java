package com.github.lomasz.spring.boot.template.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {}
