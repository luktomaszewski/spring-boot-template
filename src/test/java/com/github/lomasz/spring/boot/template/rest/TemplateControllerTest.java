package com.github.lomasz.spring.boot.template.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateEntity;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateRepository;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TemplateControllerTest {

    private static final String CREATE_PATH = "/api/templates";
    private static final String GET_BY_ID_PATH = "/api/templates/{id}";
    private static final String SEARCH_PATH = "/api/templates";

    private static final String X_REQUEST_ID_HEADER = "X-Request-ID";
    private static final String LOCATION_HEADER = "Location";

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("operation: add, should: return HttpStatus = CREATED and path in Location header, when: add correctly")
    void add() {
        // given
        Template johnDoe = new Template(null, "John Doe", "JD", BigDecimal.valueOf(182005000));

        // when
        EntityExchangeResult<byte[]> result = webTestClient.post()
                .uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(johnDoe)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(LOCATION_HEADER)
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .returnResult();

        String location = result.getResponseHeaders().getFirst(LOCATION_HEADER);

        Long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        assertThat(id).isNotNull();

        assertThat(location)
                .isNotNull()
                .contains(GET_BY_ID_PATH.replace("{id}", id.toString()));

        assertThat(location)
                .isNotNull()
                .contains(GET_BY_ID_PATH.replace("{id}", id.toString()));

        Optional<TemplateEntity> entity = templateRepository.findById(id);

        assertTrue(entity.isPresent());
        assertThat(entity.get().getId()).isEqualTo(id);
        assertThat(entity.get().getName()).isEqualTo("John Doe");
        assertThat(entity.get().getAcronym()).isEqualTo("JD");
        assertThat(entity.get().getBudget()).isEqualTo(BigDecimal.valueOf(182005000));
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse, when: acronym is null")
    void addWhenTemplateDtoWithMissingValueShouldReturnHttpStatusBadRequest() {
        // given
        Template johnDoe = new Template(null, "John Doe", null, BigDecimal.valueOf(182005000));

        // when
        webTestClient.post()
                .uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(johnDoe)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.type").isEqualTo("about:blank")
                .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.title").isEqualTo("Business Exception")
                .jsonPath("$.detail").isEqualTo("Invalid request content")
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors").value(hasSize(1))
                .jsonPath("$.errors").value(contains("acronym: must not be null"))
                .jsonPath("$.instance").isEqualTo(CREATE_PATH);
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse with many errors, when: many validation errors")
    void addWhenTemplateDtoWithManyValidationErrorsShouldReturnHttpStatusBadRequest() {
        // given
        Template johnDoe = new Template(null, null, "", BigDecimal.valueOf(-182005000));

        // when
        webTestClient.post()
                .uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(johnDoe)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.type").isEqualTo("about:blank")
                .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.title").isEqualTo("Business Exception")
                .jsonPath("$.detail").isEqualTo("Invalid request content")
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors").value(hasSize(3))
                .jsonPath("$.errors").value(containsInAnyOrder(
                        "budget: must be positive",
                        "name: must not be null",
                        "acronym: size must be between 1 and 5"
                ))
                .jsonPath("$.instance").isEqualTo(CREATE_PATH);
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse, when: budget is negative value")
    void addWhenTemplateDtoWithNegativeBudgetValueShouldReturnHttpStatusBadRequest() {
        // given
        Template johnDoe = new Template(null, "John Doe", "JD", BigDecimal.valueOf(-182005000));

        // when
        webTestClient.post()
                .uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(johnDoe)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.type").isEqualTo("about:blank")
                .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.title").isEqualTo("Business Exception")
                .jsonPath("$.detail").isEqualTo("Invalid request content")
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("budget: must be positive")
                .jsonPath("$.instance").isEqualTo(CREATE_PATH);
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse, when: too long acronym")
    void addWhenTemplateDtoWithTooLongAcronymValueShouldReturnHttpStatusBadRequest() {
        // given
        Template johnDoe = new Template(null, "John Doe", "JOHN DOE", BigDecimal.valueOf(182005000));

        // when
        webTestClient.post()
                .uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(johnDoe)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.type").isEqualTo("about:blank")
                .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.title").isEqualTo("Business Exception")
                .jsonPath("$.detail").isEqualTo("Invalid request content")
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("acronym: size must be between 1 and 5")
                .jsonPath("$.instance").isEqualTo(CREATE_PATH);
    }

    @Test
    @DisplayName("operation: getById, should: return HttpStatus = OK and Template, when: exists")
    void getByIdWhenExistsShouldReturnTemplateDtoAndHttpStatusOk() {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(182005000))
                .build();

        TemplateEntity entity = templateRepository.save(johnDoe);

        // when
        webTestClient.get()
                .uri(GET_BY_ID_PATH, entity.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.id").isEqualTo(entity.getId())
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.acronym").isEqualTo("JD")
                .jsonPath("$.budget").isEqualTo(BigDecimal.valueOf(182005000).toString());
    }

    @Test
    @DisplayName("operation: getById, should: return HttpStatus = NOT_FOUND and ErrorResponse, when: does not exist")
    void getByIdWhenDoesNotExistShouldReturnHttpStatusNotFound() {
        // given

        // when
        webTestClient.get()
                .uri(GET_BY_ID_PATH, 99L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.type").isEqualTo("about:blank")
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.title").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.detail").isEqualTo("Template with id=99 not found")
                .jsonPath("$.instance").isEqualTo(GET_BY_ID_PATH.replace("{id}", "99"))
                .jsonPath("$.timestamp").exists();
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = OK and unsorted items, when: default input")
    void searchWithDefaultInputShouldReturnUnsortedItems() {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(1000000))
                .build();

        TemplateEntity janKowalski = TemplateEntity.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(BigDecimal.valueOf(3000000))
                .build();

        TemplateEntity juanitoPerez = TemplateEntity.builder()
                .name("Juanito Perez")
                .acronym("JP")
                .budget(BigDecimal.valueOf(2000000))
                .build();

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);
        templateRepository.save(juanitoPerez);

        // when
        webTestClient.get()
                .uri(SEARCH_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.totalCount").isEqualTo(3)
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.limit").isEqualTo(20)
                .jsonPath("$.pages").isEqualTo(1)
                .jsonPath("$.items").value(hasSize(3));
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = OK and sorted by budget (asc) items, when: custom input")
    void searchWithCustomInputShouldReturnItemsSortedByBudgetAsc() {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(1000000))
                .build();

        TemplateEntity janKowalski = TemplateEntity.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(BigDecimal.valueOf(3000000))
                .build();

        TemplateEntity juanitoPerez = TemplateEntity.builder()
                .name("Juanito Perez")
                .acronym("JP")
                .budget(BigDecimal.valueOf(2000000))
                .build();

        TemplateEntity pierreEtPaul = TemplateEntity.builder()
                .name("Pierre et Paul")
                .acronym("PP")
                .budget(BigDecimal.valueOf(4000000))
                .build();

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);
        templateRepository.save(juanitoPerez);
        templateRepository.save(pierreEtPaul);

        // when
        FluxExchangeResult<SearchResult<Template>> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_PATH)
                        .queryParam("page", "1")
                        .queryParam("size", "2")
                        .queryParam("order", "DESC")
                        .queryParam("sort", "budget")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .returnResult(new ParameterizedTypeReference<>() {
                });

        SearchResult<Template> searchResult = result.getResponseBody().blockFirst();

        assertThat(searchResult).isNotNull();
        assertThat(searchResult.totalCount()).isEqualTo(4);
        assertThat(searchResult.page()).isEqualTo(1);
        assertThat(searchResult.limit()).isEqualTo(2);
        assertThat(searchResult.pages()).isEqualTo(2);
        assertThat(searchResult.items())
                .isSortedAccordingTo(
                        Comparator.comparing(Template::budget).reversed());
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = BAD_REQUEST and ErrorResponse, when: wrong sort field value")
    void searchWithWrongSortValueShouldReturnHttpStatusBadRequest() {
        // given

        // when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_PATH)
                        .queryParam("sort", "surname")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists(X_REQUEST_ID_HEADER)
                .expectBody()
                .jsonPath("$.type").isEqualTo("about:blank")
                .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.title").isEqualTo("Business Exception")
                .jsonPath("$.detail").isEqualTo("No sort property found: surname")
                .jsonPath("$.instance").isEqualTo(CREATE_PATH)
                .jsonPath("$.timestamp").exists();
    }

    @Test
    @DisplayName("should: return request with the same request id, when: provided")
    void sendRequestWithRequestIdShouldReturnTheSameRequestId() {
        // given
        String requestId = "requestId";

        // when
        webTestClient.get()
                .uri(SEARCH_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(X_REQUEST_ID_HEADER, requestId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(X_REQUEST_ID_HEADER, requestId);
    }

    @Test
    @DisplayName("should: return request with new  request id, when: not provided")
    void sendRequestWithoutRequestIdShouldReturnNewRequestId() {
        // given

        // when
        webTestClient.get()
                .uri(SEARCH_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(X_REQUEST_ID_HEADER);
    }
}
