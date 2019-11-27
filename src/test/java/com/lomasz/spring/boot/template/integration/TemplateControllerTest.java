package com.lomasz.spring.boot.template.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lomasz.spring.boot.template.filter.RequestIdFilter;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
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

        NewTemplateDto ogcNice = new NewTemplateDto(name, acronym, budget);

        // when
        MvcResult result = mvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ogcNice)))
                // then
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        Long id = Long.valueOf(result.getResponse().getHeader("Location").split("/")[4]);

        TemplateEntity savedTemplate = templateRepository.findById(id).get();

        assertNotNull(savedTemplate);
        assertThat(savedTemplate.getId()).isEqualTo(id);
        assertThat(savedTemplate.getName()).isEqualTo(name);
        assertThat(savedTemplate.getAcronym()).isEqualTo(acronym);
        assertThat(savedTemplate.getBudget()).isEqualTo(budget);
    }

    @Test
    @Transactional
    void getByIdWhenExistsShouldReturnTemplateDtoAndHttpStatusOk() throws Exception {
        // given
        String templateName = "name";
        String templateAcronym = "NA";
        Long templateBudget = 182005000L;

        TemplateEntity ogcNice = new TemplateEntity();
        ogcNice.setName(templateName);
        ogcNice.setAcronym(templateAcronym);
        ogcNice.setBudget(templateBudget);

        TemplateEntity savedEntity = templateRepository.save(ogcNice);
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
    @Transactional
    void addWhenNewTemplateDtoWithMissingValueShouldReturnHttpStatusBadRequest() throws Exception {
        // given
        String templateName = "name";
        Long templateBudget = 182005000L;

        NewTemplateDto ogcNice = new NewTemplateDto(templateName, null, templateBudget);

        // when
        mvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ogcNice)))
                // then
                .andExpect(status().isBadRequest());

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
    void getListWithDefaultInputShouldReturnUnsortedItems() throws Exception {
        // given
        TemplateEntity ogcNice = new TemplateEntity();
        ogcNice.setName("name");
        ogcNice.setAcronym("NA");
        ogcNice.setBudget(1000000L);

        TemplateEntity psg = new TemplateEntity();
        psg.setName("Paris Saint-Germain");
        psg.setAcronym("PSG");
        psg.setBudget(3000000L);

        TemplateEntity olympicLyon = new TemplateEntity();
        olympicLyon.setName("Olympique Lyon");
        olympicLyon.setAcronym("OL");
        olympicLyon.setBudget(2000000L);

        templateRepository.save(ogcNice);
        templateRepository.save(psg);
        templateRepository.save(olympicLyon);

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
    void getListWithCustomInputShouldReturnItemsSortedByBudgetAsc() throws Exception {
        // given
        TemplateEntity ogcNice = new TemplateEntity();
        ogcNice.setName("name");
        ogcNice.setAcronym("NA");
        ogcNice.setBudget(1000000L);

        TemplateEntity psg = new TemplateEntity();
        psg.setName("Paris Saint-Germain");
        psg.setAcronym("PSG");
        psg.setBudget(3000000L);

        TemplateEntity olympicLyon = new TemplateEntity();
        olympicLyon.setName("Olympique Lyon");
        olympicLyon.setAcronym("OL");
        olympicLyon.setBudget(2000000L);

        TemplateEntity asMonaco = new TemplateEntity();
        asMonaco.setName("AS Monaco");
        asMonaco.setAcronym("ASM");
        asMonaco.setBudget(4000000L);

        templateRepository.save(ogcNice);
        templateRepository.save(psg);
        templateRepository.save(olympicLyon);
        templateRepository.save(asMonaco);

        // when
        MvcResult result = mvc.perform(get(GET_LIST_URL)
                .param("page", "1")
                .param("size", "2")
                .param("sort", "budget,desc")
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
