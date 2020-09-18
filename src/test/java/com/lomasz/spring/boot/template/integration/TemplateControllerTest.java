package com.lomasz.spring.boot.template.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lomasz.spring.boot.template.filter.RequestIdFilter;
import com.lomasz.spring.boot.template.model.dto.ErrorDto;
import com.lomasz.spring.boot.template.model.dto.NewTemplateDto;
import com.lomasz.spring.boot.template.model.dto.SearchResult;
import com.lomasz.spring.boot.template.model.dto.TemplateDto;
import com.lomasz.spring.boot.template.model.entity.TemplateEntity;
import com.lomasz.spring.boot.template.repository.TemplateRepository;
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

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplateControllerTest {

    private static final String GET_LIST_URL = "/api";
    private static final String GET_BY_ID_URL = "/api/{id}";
    private static final String CREATE_URL = "/api";

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
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(requestIdFilter)
                .build();
    }

    @Test
    @Transactional
    void add() throws Exception {
        // given
        String name = "John Doe";
        String acronym = "JD";
        Long budget = 182005000L;

        NewTemplateDto johnDoe = new NewTemplateDto(name, acronym, budget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        Long id = Long.valueOf(result.getResponse().getHeader("Location").split("/")[4]);

        Optional<TemplateEntity> savedTemplate = templateRepository.findById(id);

        assertTrue(savedTemplate.isPresent());
        assertThat(savedTemplate.get().getId()).isEqualTo(id);
        assertThat(savedTemplate.get().getName()).isEqualTo(name);
        assertThat(savedTemplate.get().getAcronym()).isEqualTo(acronym);
        assertThat(savedTemplate.get().getBudget()).isEqualTo(budget);
    }

    @Test
    @Transactional
    void addWhenNewTemplateDtoWithMissingValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "John Doe";
        Long templateBudget = 182005000L;

        NewTemplateDto johnDoe = new NewTemplateDto(templateName, null, templateBudget);

        // when
        mvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addWhenNewTemplateDtoWithNegativeBudgetValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "John Doe";
        String templateAcronym = "JD";
        Long templateBudget = -182005000L;

        NewTemplateDto johnDoe = new NewTemplateDto(templateName, templateAcronym, templateBudget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        List<ErrorDto> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ErrorDto>>(){});

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).getMessage()).isEqualTo("Wrong value in the field: budget");
        assertThat(responseBody.get(0).getDetails()).isEqualTo("The value must be positive");
    }

    @Test
    @Transactional
    void addWhenNewTemplateDtoWithTooLongAcronymValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "John Doe";
        String templateAcronym = "JOHN DOE";
        Long templateBudget = 182005000L;

        NewTemplateDto johnDoe = new NewTemplateDto(templateName, templateAcronym, templateBudget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(johnDoe)))
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        List<ErrorDto> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ErrorDto>>(){});

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).getMessage()).isEqualTo("Wrong value in the field: acronym");
        assertThat(responseBody.get(0).getDetails()).isEqualTo("size must be between 0 and 5");
    }

    @Test
    @Transactional
    void getByIdWhenExistsShouldReturnTemplateDtoAndHttpStatusOk() throws Exception {
        // given
        String templateName = "John Doe";
        String templateAcronym = "JD";
        Long templateBudget = 182005000L;

        TemplateEntity johnDoe = new TemplateEntity();
        johnDoe.setName(templateName);
        johnDoe.setAcronym(templateAcronym);
        johnDoe.setBudget(templateBudget);

        TemplateEntity savedEntity = templateRepository.save(johnDoe);
        Long id = savedEntity.getId();

        // when
        MvcResult result = mvc.perform(get(GET_BY_ID_URL, id)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andReturn();

        TemplateDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), TemplateDto.class);

        assertThat(responseBody.getId()).isEqualTo(id);
        assertThat(responseBody.getName()).isEqualTo(templateName);
        assertThat(responseBody.getAcronym()).isEqualTo(templateAcronym);
        assertThat(responseBody.getBudget()).isEqualTo(templateBudget);
    }

    @Test
    void getByIdWhenDoesntExistShouldReturnHttpStatusNotFound() throws Exception {
        // given
        Long id = 99L;

        // when
        mvc.perform(get(GET_BY_ID_URL, id)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchWithDefaultInputShouldReturnUnsortedItems() throws Exception {
        // given
        TemplateEntity johnDoe = new TemplateEntity();
        johnDoe.setName("John Doe");
        johnDoe.setAcronym("JD");
        johnDoe.setBudget(1000000L);

        TemplateEntity janKowalski = new TemplateEntity();
        janKowalski.setName("Jan Kowalski");
        janKowalski.setAcronym("JK");
        janKowalski.setBudget(3000000L);

        TemplateEntity juanitoPerez = new TemplateEntity();
        juanitoPerez.setName("Juantio Pérez");
        juanitoPerez.setAcronym("JP");
        juanitoPerez.setBudget(2000000L);

        templateRepository.save(johnDoe);
        templateRepository.save(janKowalski);
        templateRepository.save(juanitoPerez);

        // when
        MvcResult result = mvc.perform(get(GET_LIST_URL)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andReturn();

        SearchResult<TemplateDto> searchResult = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<SearchResult<TemplateDto>>() {
                });

        assertThat(searchResult.getTotalCount()).isEqualTo(3);
        assertThat(searchResult.getPage()).isEqualTo(0);
        assertThat(searchResult.getLimit()).isEqualTo(20);
        assertThat(searchResult.getPages()).isEqualTo(1);
        assertThat(searchResult.getItems()).hasSize(3);
    }

    @Test
    @Transactional
    void searchWithCustomInputShouldReturnItemsSortedByBudgetAsc() throws Exception {
        // given
        TemplateEntity johnDoe = new TemplateEntity();
        johnDoe.setName("John Doe");
        johnDoe.setAcronym("JD");
        johnDoe.setBudget(1000000L);

        TemplateEntity janKowalski = new TemplateEntity();
        janKowalski.setName("Jan Kowalski");
        janKowalski.setAcronym("JK");
        janKowalski.setBudget(3000000L);

        TemplateEntity juanitoPerez = new TemplateEntity();
        juanitoPerez.setName("Juantio Pérez");
        juanitoPerez.setAcronym("JP");
        juanitoPerez.setBudget(2000000L);

        TemplateEntity pierreEtPaul = new TemplateEntity();
        pierreEtPaul.setName("Pierre et Paul");
        pierreEtPaul.setAcronym("PP");
        pierreEtPaul.setBudget(4000000L);

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

        SearchResult<TemplateDto> searchResult = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<SearchResult<TemplateDto>>() {
                });

        assertThat(searchResult.getTotalCount()).isEqualTo(4);
        assertThat(searchResult.getPage()).isEqualTo(1);
        assertThat(searchResult.getLimit()).isEqualTo(2);
        assertThat(searchResult.getPages()).isEqualTo(2);
        assertThat(searchResult.getItems()).isSortedAccordingTo(Comparator.comparingLong(TemplateDto::getBudget).reversed());
    }

    @Test
    void searchWithWrongSortValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given

        // when
        mvc.perform(get(GET_LIST_URL)
                .param("sort", "surname")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());

    }

    @Test
    void sendRequestWithRequestIdShouldReturnTheSameRequestId() throws Exception {
        // given
        String requestId = "requestId";

        // when
        mvc.perform(get(GET_LIST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", requestId))
                // then
                .andExpect(header().string("X-Request-ID", requestId));
    }

    @Test
    void sendRequestWithoutRequestIdShouldReturnNewRequestId() throws Exception {
        // given

        // when
        mvc.perform(get(GET_LIST_URL)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(header().exists("X-Request-ID"));
    }

}
