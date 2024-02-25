package com.github.lomasz.spring.boot.template.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateEntity;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateRepository;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.SearchTemplatePort;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchTemplatesUseCaseTest {

    @Autowired
    private SearchTemplatePort searchTemplatePort;

    @Autowired
    private TemplateRepository templateRepository;

    private SearchTemplatesUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new SearchTemplatesUseCase(searchTemplatePort);
    }

    @Test
    @DisplayName("should: return sorted items")
    @Transactional
    void shouldReturnSortedItems() {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(100000L)
                .build();

        TemplateEntity janKowalski = TemplateEntity.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(200000L)
                .build();

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);

        // when
        SearchTemplatesUseCase.Output result = sut.execute(
                new SearchTemplatesUseCase.Input(0, 20, SortDirection.ASC, "budget"));

        // then
        assertThat(result.result()).isNotNull();
        assertThat(result.result().getLimit()).isEqualTo(20);
        assertThat(result.result().getPage()).isEqualTo(0);
        assertThat(result.result().getPages()).isEqualTo(1);
        assertThat(result.result().getTotalCount()).isEqualTo(2);
        assertThat(result.result().getItems())
                .hasSize(2)
                .hasOnlyElementsOfType(Template.class)
                .isSortedAccordingTo(Comparator.comparing(Template::getBudget));
    }

}
