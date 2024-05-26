package com.github.lomasz.spring.boot.template.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateEntity;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateRepository;
import com.github.lomasz.spring.boot.template.application.domain.model.SortDirection;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.SearchTemplatePort;
import com.github.lomasz.spring.boot.template.application.usecase.SearchTemplatesUseCase;
import java.math.BigDecimal;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    void shouldReturnSortedItems() {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(100000L))
                .build();

        TemplateEntity janKowalski = TemplateEntity.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(BigDecimal.valueOf(200000))
                .build();

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);

        // when
        SearchTemplatesUseCase.Output result = sut.execute(
                new SearchTemplatesUseCase.Input(0, 20, SortDirection.ASC, "budget"));

        // then
        assertThat(result.searchResult()).isNotNull();
        assertThat(result.searchResult().limit()).isEqualTo(20);
        assertThat(result.searchResult().page()).isEqualTo(0);
        assertThat(result.searchResult().pages()).isEqualTo(1);
        assertThat(result.searchResult().totalCount()).isEqualTo(2);
        assertThat(result.searchResult().items())
                .hasSize(2)
                .hasOnlyElementsOfType(Template.class)
                .isSortedAccordingTo(Comparator.comparing(Template::budget));
    }

}
