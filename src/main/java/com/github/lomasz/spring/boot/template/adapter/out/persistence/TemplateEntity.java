package com.github.lomasz.spring.boot.template.adapter.out.persistence;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "template")
public class TemplateEntity {

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
    private BigDecimal budget;

    public static TemplateEntity fromDomain(Template template) {
        return TemplateEntity.builder()
                .name(template.name())
                .budget(template.budget())
                .acronym(template.acronym())
                .build();
    }

    public Template toDomain() {
        return new Template(this.id, this.name, this.acronym, this.budget);
    }
}
