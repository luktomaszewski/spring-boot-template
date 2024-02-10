package com.github.lomasz.spring.boot.template.application.port;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;

public interface AddTemplatePort {

    Long create(NewTemplate newTemplate);

}
