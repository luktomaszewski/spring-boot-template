package com.github.lomasz.spring.boot.template.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.port.TemplateStorage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AddTemplateUseCaseTest {

    @Autowired
    private TemplateStorage templateStorage;

    private AddTemplateUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new AddTemplateUseCase(templateStorage);
    }

    @Test
    @Transactional
    void shouldCreate() {
        // given
        NewTemplate newTemplate = NewTemplate.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(100000L)
                .build();

        // when
        AddTemplateUseCase.Output result = sut.execute(new AddTemplateUseCase.Input(newTemplate));

        // then
        assertThat(result.id()).isNotNull();
    }
}
