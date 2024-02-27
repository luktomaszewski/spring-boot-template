package com.github.lomasz.spring.boot.template.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateEntity;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateRepository;
import com.github.lomasz.spring.boot.template.application.domain.exception.NotFoundException;
import com.github.lomasz.spring.boot.template.application.port.GetTemplatePort;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GetTemplateUseCaseTest {

    @Autowired
    private GetTemplatePort getTemplatePort;

    @Autowired
    private TemplateRepository templateRepository;

    private GetTemplateUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new GetTemplateUseCase(getTemplatePort);
    }

    @Test
    @DisplayName("should: return template, when: exists")
    @Transactional
    void shouldGetByIdWhenExists() {
        // given
        TemplateEntity newTemplate = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(100000))
                .build();

        TemplateEntity saved = templateRepository.save(newTemplate);

        // when
        GetTemplateUseCase.Output result = sut.execute(new GetTemplateUseCase.Input(saved.getId()));

        // then
        assertThat(result.template()).isNotNull();
        assertThat(result.template().id()).isEqualTo(saved.getId());
        assertThat(result.template().name()).isEqualTo("John Doe");
        assertThat(result.template().acronym()).isEqualTo("JD");
        assertThat(result.template().budget()).isEqualTo(BigDecimal.valueOf(100000));
    }

    @Test
    @DisplayName("should: throw NotFoundException, when: does not exist")
    void shouldThrowNotFoundExceptionWhenDoesNotExist() {
        // given

        // when
        // then
        assertThrows(NotFoundException.class, () -> sut.execute(new GetTemplateUseCase.Input(1L)));
    }

}
