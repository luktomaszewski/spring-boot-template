package com.github.lomasz.spring.boot.template.adapter.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class ErrorDto {

    private String message;
    private String details;

    public ErrorDto(String message) {
        this.message = message;
    }
}
