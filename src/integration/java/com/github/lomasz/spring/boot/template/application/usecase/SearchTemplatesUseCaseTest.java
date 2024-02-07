package com.github.lomasz.spring.boot.template.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.TemplateStorage;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchTemplatesUseCaseTest {

    @Autowired
    private TemplateStorage templateStorage;

    private SearchTemplatesUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new SearchTemplatesUseCase(templateStorage);
    }

    @Test
    @Transactional
    void shouldReturnSortedItems() {
        // given
        NewTemplate johnDoe = NewTemplate.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(100000L)
                .build();

        NewTemplate janKowalski = NewTemplate.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(200000L)
                .build();

        templateStorage.create(johnDoe);
        templateStorage.create(janKowalski);

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
