package com.github.lomasz.spring.boot.template.application.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class NewTemplate {

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 5)
    private String acronym;

    @NotNull
    @Min(value = 0L, message = "The value must be positive")
    private Long budget;
}
