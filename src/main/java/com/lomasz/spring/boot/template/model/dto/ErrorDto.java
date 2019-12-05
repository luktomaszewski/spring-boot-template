package com.lomasz.spring.boot.template.model.dto;

import lombok.Data;

@Data
public class ErrorDto {

    private String message;

    private String details;

    public ErrorDto(String message) {
        this.message = message;
    }
}
