package com.github.lomasz.spring.boot.template.model.entity;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
public class TemplateEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5994238256683599906L;

    private static final String SEQUENCE_NAME = "template_seq";

    @Id
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 5, nullable = false)
    private String acronym;

    @Column(nullable = false)
    private Long budget;
}