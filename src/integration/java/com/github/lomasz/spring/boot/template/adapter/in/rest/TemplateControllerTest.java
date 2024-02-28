package com.github.lomasz.spring.boot.template.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateEntity;
import com.github.lomasz.spring.boot.template.adapter.out.persistence.TemplateRepository;
import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.domain.model.SearchResult;
import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplateControllerTest {

    private static final String CREATE_PATH = "/api/templates";
    private static final String GET_BY_ID_PATH = "/api/templates/{id}";
    private static final String SEARCH_PATH = "/api/templates";

    private static final String X_REQUEST_ID_HEADER = "X-Request-ID";
    private static final String LOCATION_HEADER = "Location";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private RequestIdFilter requestIdFilter;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(requestIdFilter)
                .build();
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = CREATED and path in Location header, when: add correctly")
    @Transactional
    void add() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate("John Doe", "JD", BigDecimal.valueOf(182005000));

        // when
        MvcResult result = mvc.perform(post(CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION_HEADER))
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andReturn();

        String location = result.getResponse().getHeader(LOCATION_HEADER);

        Long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        assertThat(id).isNotNull();

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
    @Transactional
    void addWhenNewTemplateDtoWithMissingValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate("John Doe", null, BigDecimal.valueOf(182005000));

        // when
        mvc.perform(post(CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", contains("acronym: must not be null")))
                .andExpect(jsonPath("$.instance").value(CREATE_PATH));
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse with many errors, when: many validation errors")
    @Transactional
    void addWhenNewTemplateDtoWithManyValidationErrorsShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate(null, "", BigDecimal.valueOf(-182005000));

        // when
        mvc.perform(post(CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "budget: must be positive",
                        "name: must not be null",
                        "acronym: size must be between 1 and 5"
                )))
                .andExpect(jsonPath("$.instance").value(CREATE_PATH));
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse, when: budget is negative value")
    @Transactional
    void addWhenNewTemplateDtoWithNegativeBudgetValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate("John Doe", "JD", BigDecimal.valueOf(-182005000));

        // when
        mvc.perform(post(CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", contains("budget: must be positive")))
                .andExpect(jsonPath("$.instance").value(CREATE_PATH));
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = BAD_REQUEST and ErrorResponse, when: too long acronym")
    @Transactional
    void addWhenNewTemplateDtoWithTooLongAcronymValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate( "John Doe", "JOHN DOE", BigDecimal.valueOf(182005000));

        // when
        mvc.perform(post(CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", contains("acronym: size must be between 1 and 5")))
                .andExpect(jsonPath("$.instance").value(CREATE_PATH));
    }

    @Test
    @DisplayName("operation: getById, should: return HttpStatus = CREATED and Template, when: exists")
    @Transactional
    void getByIdWhenExistsShouldReturnTemplateDtoAndHttpStatusOk() throws Exception {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(BigDecimal.valueOf(182005000))
                .build();

        TemplateEntity entity = templateRepository.save(johnDoe);

        // when
        mvc.perform(get(GET_BY_ID_PATH, entity.getId()).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.acronym").value("JD"))
                .andExpect(jsonPath("$.budget").value(BigDecimal.valueOf(182005000)));
    }

    @Test
    @DisplayName("operation: getById, should: return HttpStatus = NOT_FOUND and ErrorResponse, when: does not exist")
    void getByIdWhenDoesNotExistShouldReturnHttpStatusNotFound() throws Exception {
        // given

        // when
        mvc.perform(get(GET_BY_ID_PATH, 99L).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.title").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.detail").value("Template with id=99 not found"))
                .andExpect(jsonPath("$.instance").value(GET_BY_ID_PATH.replace("{id}", "99")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = CREATED and unsorted items, when: default input")
    @Transactional
    void searchWithDefaultInputShouldReturnUnsortedItems() throws Exception {
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
        mvc.perform(get(SEARCH_PATH).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.totalCount").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.limit").value(20))
                .andExpect(jsonPath("$.pages").value(1))
                .andExpect(jsonPath("$.items").value(hasSize(3)));
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = CREATED and sorted by budget (asc) items, when: custom input")
    @Transactional
    void searchWithCustomInputShouldReturnItemsSortedByBudgetAsc() throws Exception {
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
        MvcResult result = mvc.perform(get(SEARCH_PATH)
                        .param("page", "1")
                        .param("size", "2")
                        .param("order", "DESC")
                        .param("sort", "budget")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andReturn();

        SearchResult<Template> searchResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

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
    void searchWithWrongSortValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given

        // when
        mvc.perform(get(SEARCH_PATH).param("sort", "surname").contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("No sort property found: surname"))
                .andExpect(jsonPath("$.instance").value(CREATE_PATH))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should: return request with the same request id, when: provided")
    void sendRequestWithRequestIdShouldReturnTheSameRequestId() throws Exception {
        // given
        String requestId = "requestId";

        // when
        mvc.perform(get(SEARCH_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUEST_ID_HEADER, requestId))
                // then
                .andExpect(header().string(X_REQUEST_ID_HEADER, requestId));
    }

    @Test
    @DisplayName("should: return request with new  request id, when: not provided")
    void sendRequestWithoutRequestIdShouldReturnNewRequestId() throws Exception {
        // given

        // when
        mvc.perform(get(SEARCH_PATH).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(header().exists(X_REQUEST_ID_HEADER));
    }
}
