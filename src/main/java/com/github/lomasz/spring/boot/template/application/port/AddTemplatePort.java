package com.github.lomasz.spring.boot.template.application.port;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;

public interface AddTemplatePort {

    Long create(Template template);

}
