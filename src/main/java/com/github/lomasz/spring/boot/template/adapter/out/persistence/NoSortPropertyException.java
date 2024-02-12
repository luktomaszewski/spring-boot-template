package com.github.lomasz.spring.boot.template.adapter.out.persistence;

import com.github.lomasz.spring.boot.template.application.domain.exception.BusinessException;
import java.io.Serial;

class NoSortPropertyException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -2582814506546135357L;

    public NoSortPropertyException(String message) {
        super(message);
    }
}
