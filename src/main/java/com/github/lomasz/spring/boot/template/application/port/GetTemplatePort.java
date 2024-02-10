package com.github.lomasz.spring.boot.template.application.port;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import java.util.Optional;

public interface GetTemplatePort {

    Optional<Template> findById(Long id);

}
