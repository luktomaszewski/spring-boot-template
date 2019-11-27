package com.lomasz.spring.boot.template.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewTemplateDto {

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
