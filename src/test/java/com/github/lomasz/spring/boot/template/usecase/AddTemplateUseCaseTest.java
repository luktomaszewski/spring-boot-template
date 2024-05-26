package com.github.lomasz.spring.boot.template.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateEntity;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateRepository;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.AddTemplatePort;
import com.github.lomasz.spring.boot.template.application.usecase.AddTemplateUseCase;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AddTemplateUseCaseTest {

    @Autowired
    private AddTemplatePort addTemplatePort;

    @Autowired
    private TemplateRepository templateRepository;

    private AddTemplateUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new AddTemplateUseCase(addTemplatePort);
    }

    @Test
    @DisplayName("should: create new template")
    void shouldCreate() {
        // given
        Template Template = new Template(null, "John Doe", "JD", BigDecimal.valueOf(100000));

        // when
        AddTemplateUseCase.Output result = sut.execute(new AddTemplateUseCase.Input(Template));

        // then
        assertThat(result.id()).isNotNull();

        Optional<TemplateEntity> entity = templateRepository.findById(result.id());

        assertThat(entity).isNotEmpty();
        assertThat(entity.get().getId()).isEqualTo(result.id());
        assertThat(entity.get().getName()).isEqualTo("John Doe");
        assertThat(entity.get().getAcronym()).isEqualTo("JD");
        assertThat(entity.get().getBudget()).isEqualTo(BigDecimal.valueOf(100000));
    }
}
