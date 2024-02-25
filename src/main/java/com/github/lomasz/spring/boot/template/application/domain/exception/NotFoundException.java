package com.github.lomasz.spring.boot.template.application.domain.exception;

import java.io.Serial;

public class NotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -2582819800646125357L;

    public NotFoundException(String message) {
        super(message);
    }
}
