package com.github.lomasz.spring.boot.template.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
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

    private static final String CREATE_URL = "/api/templates";
    private static final String GET_BY_ID_URL = "/api/templates/{id}";
    private static final String SEARCH_URL = "/api/templates";

    private static final String X_REQUEST_ID = "X-Request-ID";

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
    @DisplayName("operation: add, should: return HttpStatus = 201 and Location header, when: add correctly")
    @Transactional
    void add() throws Exception {
        // given
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        NewTemplate johnDoe = new NewTemplate(name, acronym, budget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists(X_REQUEST_ID))
                .andReturn();

        String location = result.getResponse().getHeader("Location");

        Long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        assertThat(id).isNotNull();

        assertThat(location)
                .isNotNull()
                .contains(GET_BY_ID_URL.replace("{id}", id.toString()));

        Optional<TemplateEntity> entity = templateRepository.findById(id);

        assertTrue(entity.isPresent());
        assertThat(entity.get().getId()).isEqualTo(id);
        assertThat(entity.get().getName()).isEqualTo(name);
        assertThat(entity.get().getAcronym()).isEqualTo(acronym);
        assertThat(entity.get().getBudget()).isEqualTo(budget);
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = 400 and ErrorResponse, when: acronym is null")
    @Transactional
    void addWhenNewTemplateDtoWithMissingValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate("John Doe", null, 182005000L);

        // when
        mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", contains("acronym: must not be null")))
                .andExpect(jsonPath("$.instance").value(CREATE_URL));
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = 400 and ErrorResponse, when: budget is negative value")
    @Transactional
    void addWhenNewTemplateDtoWithNegativeBudgetValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate("John Doe", "JD", -182005000L);

        // when
        mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", contains("budget: The value must be positive")))
                .andExpect(jsonPath("$.instance").value(CREATE_URL));
    }

    @Test
    @DisplayName("operation: add, should: return HttpStatus = 400 and ErrorResponse, when: too long acronym")
    @Transactional
    void addWhenNewTemplateDtoWithTooLongAcronymValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        NewTemplate johnDoe = new NewTemplate( "John Doe", "JOHN DOE", 182005000L);

        // when
        mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", contains("acronym: size must be between 0 and 5")))
                .andExpect(jsonPath("$.instance").value(CREATE_URL));
    }

    @Test
    @DisplayName("operation: getById, should: return HttpStatus = 200 and Template, when: exists")
    @Transactional
    void getByIdWhenExistsShouldReturnTemplateDtoAndHttpStatusOk() throws Exception {
        // given
        String templateName = "John Doe";
        String templateAcronym = "JD";
        Long templateBudget = 182005000L;

        TemplateEntity johnDoe = TemplateEntity.builder()
                .name(templateName)
                .acronym(templateAcronym)
                .budget(templateBudget)
                .build();

        TemplateEntity entity = templateRepository.save(johnDoe);

        // when
        mvc.perform(get(GET_BY_ID_URL, entity.getId()).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(header().exists(X_REQUEST_ID))
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.name").value(templateName))
                .andExpect(jsonPath("$.acronym").value(templateAcronym))
                .andExpect(jsonPath("$.budget").value(templateBudget));
    }

    @Test
    @DisplayName("operation: getById, should: return HttpStatus = 404 and ErrorResponse, when: doesn't exist")
    void getByIdWhenDoesntExistShouldReturnHttpStatusNotFound() throws Exception {
        // given

        // when
        mvc.perform(get(GET_BY_ID_URL, 99L).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(header().exists(X_REQUEST_ID))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.title").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.detail").value("Template with id=99 not found"))
                .andExpect(jsonPath("$.instance").value(GET_BY_ID_URL.replace("{id}", "99")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = 200 and unsorted items, when: default input")
    @Transactional
    void searchWithDefaultInputShouldReturnUnsortedItems() throws Exception {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(1000000L)
                .build();

        TemplateEntity janKowalski = TemplateEntity.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(3000000L)
                .build();

        TemplateEntity juanitoPerez = TemplateEntity.builder()
                .name("Juanito Perez")
                .acronym("JP")
                .budget(2000000L)
                .build();

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);
        templateRepository.save(juanitoPerez);

        // when
        MvcResult result = mvc.perform(get(SEARCH_URL).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(header().exists(X_REQUEST_ID))
                .andReturn();

        SearchResult<Template> searchResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertThat(searchResult.getTotalCount()).isEqualTo(3);
        assertThat(searchResult.getPage()).isEqualTo(0);
        assertThat(searchResult.getLimit()).isEqualTo(20);
        assertThat(searchResult.getPages()).isEqualTo(1);
        assertThat(searchResult.getItems()).hasSize(3);
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = 200 and sorted by budget (asc) items, when: custom input")
    @Transactional
    void searchWithCustomInputShouldReturnItemsSortedByBudgetAsc() throws Exception {
        // given
        TemplateEntity johnDoe = TemplateEntity.builder()
                .name("John Doe")
                .acronym("JD")
                .budget(1000000L)
                .build();

        TemplateEntity janKowalski = TemplateEntity.builder()
                .name("Jan Kowalski")
                .acronym("JK")
                .budget(3000000L)
                .build();

        TemplateEntity juanitoPerez = TemplateEntity.builder()
                .name("Juanito Perez")
                .acronym("JP")
                .budget(2000000L)
                .build();

        TemplateEntity pierreEtPaul = TemplateEntity.builder()
                .name("Pierre et Paul")
                .acronym("PP")
                .budget(4000000L)
                .build();

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);
        templateRepository.save(juanitoPerez);
        templateRepository.save(pierreEtPaul);

        // when
        MvcResult result = mvc.perform(get(SEARCH_URL)
                        .param("page", "1")
                        .param("size", "2")
                        .param("order", "DESC")
                        .param("sort", "budget")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(header().exists(X_REQUEST_ID))
                .andReturn();

        SearchResult<Template> searchResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertThat(searchResult.getTotalCount()).isEqualTo(4);
        assertThat(searchResult.getPage()).isEqualTo(1);
        assertThat(searchResult.getLimit()).isEqualTo(2);
        assertThat(searchResult.getPages()).isEqualTo(2);
        assertThat(searchResult.getItems())
                .isSortedAccordingTo(
                        Comparator.comparingLong(Template::getBudget).reversed());
    }

    @Test
    @DisplayName("operation: search, should: return HttpStatus = 400 and ErrorResponse, when: wrong sort field value")
    void searchWithWrongSortValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given

        // when
        mvc.perform(get(SEARCH_URL).param("sort", "surname").contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(X_REQUEST_ID))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Business Exception"))
                .andExpect(jsonPath("$.detail").value("No sort property found: surname"))
                .andExpect(jsonPath("$.instance").value(CREATE_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should: return request with the same request id, when: provided")
    void sendRequestWithRequestIdShouldReturnTheSameRequestId() throws Exception {
        // given
        String requestId = "requestId";

        // when
        mvc.perform(get(SEARCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUEST_ID, requestId))
                // then
                .andExpect(header().string(X_REQUEST_ID, requestId));
    }

    @Test
    @DisplayName("should: return request with new  request id, when: not provided")
    void sendRequestWithoutRequestIdShouldReturnNewRequestId() throws Exception {
        // given

        // when
        mvc.perform(get(SEARCH_URL).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(header().exists(X_REQUEST_ID));
    }
}
