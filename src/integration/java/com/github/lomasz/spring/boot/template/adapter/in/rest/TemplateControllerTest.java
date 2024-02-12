package com.github.lomasz.spring.boot.template.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplateControllerTest {

    private static final String GET_LIST_URL = "/api/templates";
    private static final String GET_BY_ID_URL = "/api/templates/{id}";
    private static final String CREATE_URL = "/api/templates";

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
    @Transactional
    public void add() throws Exception {
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
                .andReturn();

        String location = result.getResponse().getHeader("Location");

        Long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));

        String expectedLocation = "http://localhost/api/templates/" + id;

        assertThat(location)
                .isNotNull()
                .isEqualTo(expectedLocation);

        Optional<TemplateEntity> entity = templateRepository.findById(id);

        assertTrue(entity.isPresent());
        assertThat(entity.get().getId()).isEqualTo(id);
        assertThat(entity.get().getName()).isEqualTo(name);
        assertThat(entity.get().getAcronym()).isEqualTo(acronym);
        assertThat(entity.get().getBudget()).isEqualTo(budget);
    }

    @Test
    @Transactional
    public void addWhenNewTemplateDtoWithMissingValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "John Doe";
        Long templateBudget = 182005000L;

        NewTemplate johnDoe = new NewTemplate(templateName, null, templateBudget);

        // when
        mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void addWhenNewTemplateDtoWithNegativeBudgetValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "John Doe";
        String templateAcronym = "JD";
        Long templateBudget = -182005000L;

        NewTemplate johnDoe = new NewTemplate(templateName, templateAcronym, templateBudget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        List<ErrorDto> responseBody = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).getMessage()).isEqualTo("Wrong value in the field: budget");
        assertThat(responseBody.get(0).getDetails()).isEqualTo("The value must be positive");
    }

    @Test
    @Transactional
    public void addWhenNewTemplateDtoWithTooLongAcronymValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "John Doe";
        String templateAcronym = "JOHN DOE";
        Long templateBudget = 182005000L;

        NewTemplate johnDoe = new NewTemplate(templateName, templateAcronym, templateBudget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        List<ErrorDto> responseBody = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).getMessage()).isEqualTo("Wrong value in the field: acronym");
        assertThat(responseBody.get(0).getDetails()).isEqualTo("size must be between 0 and 5");
    }

    @Test
    @Transactional
    public void getByIdWhenExistsShouldReturnTemplateDtoAndHttpStatusOk() throws Exception {
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
        MvcResult result = mvc.perform(get(GET_BY_ID_URL, entity.getId()).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andReturn();

        Template responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), Template.class);

        assertThat(responseBody.getId()).isEqualTo(entity.getId());
        assertThat(responseBody.getName()).isEqualTo(templateName);
        assertThat(responseBody.getAcronym()).isEqualTo(templateAcronym);
        assertThat(responseBody.getBudget()).isEqualTo(templateBudget);
    }

    @Test
    void getByIdWhenDoesntExistShouldReturnHttpStatusNotFound() throws Exception {
        // given
        Long id = 99L;

        // when
        mvc.perform(get(GET_BY_ID_URL, id).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void searchWithDefaultInputShouldReturnUnsortedItems() throws Exception {
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
        MvcResult result = mvc.perform(get(GET_LIST_URL).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
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
    @Transactional
    public void searchWithCustomInputShouldReturnItemsSortedByBudgetAsc() throws Exception {
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
        MvcResult result = mvc.perform(get(GET_LIST_URL)
                        .param("page", "1")
                        .param("size", "2")
                        .param("order", "DESC")
                        .param("sort", "budget")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
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
    void searchWithWrongSortValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given

        // when
        mvc.perform(get(GET_LIST_URL).param("sort", "surname").contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendRequestWithRequestIdShouldReturnTheSameRequestId() throws Exception {
        // given
        String requestId = "requestId";

        // when
        mvc.perform(get(GET_LIST_URL).contentType(MediaType.APPLICATION_JSON).header("X-Request-ID", requestId))
                // then
                .andExpect(header().string("X-Request-ID", requestId));
    }

    @Test
    void sendRequestWithoutRequestIdShouldReturnNewRequestId() throws Exception {
        // given

        // when
        mvc.perform(get(GET_LIST_URL).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(header().exists("X-Request-ID"));
    }
}
