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
class GetTemplateUseCaseTest {

    @Autowired
    private TemplateStorage templateStorage;

    private GetTemplateUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new GetTemplateUseCase(templateStorage);
    }

    @Test
    @Transactional
    void shouldGetByIdWhenExists() {
        // given
        NewTemplate newTemplate = NewTemplate.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(100000L)
                .build();

        Long id = templateStorage.create(newTemplate);

        // when
        GetTemplateUseCase.Output result = sut.execute(new GetTemplateUseCase.Input(id));

        // then
        assertThat(result.template()).isNotEmpty();
        assertThat(result.template().get().getId()).isEqualTo(id);
        assertThat(result.template().get().getName()).isEqualTo("John Doe");
        assertThat(result.template().get().getAcronym()).isEqualTo("JD");
        assertThat(result.template().get().getBudget()).isEqualTo(100000L);
    }

    @Test
    void shouldBeEmptyWhenDoesntExist() {
        // given

        // when
        GetTemplateUseCase.Output result = sut.execute(new GetTemplateUseCase.Input(1L));

        // then
        assertThat(result.template()).isEmpty();
    }

}
