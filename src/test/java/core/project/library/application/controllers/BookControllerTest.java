package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.mappers.EntityMapper;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerTest {

    MockMvc mockMvc;
    @Autowired
    BookController bookController;
    @Autowired
    Optional<EntityMapper> entityMapper;
    @Autowired
    BookService bookService;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookRepository.RowToBook rowToBook;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .build();
    }

    @Test
    @Order(1)
    void getBookByIdTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/findById/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("Title")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("9781861972712")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(43)))
                .andExpect(jsonPath("$.publisher.publisherName.publisherName", is("Publisher")))
                .andExpect(jsonPath("$.authors[0].firstName.firstName", is("Author")))
                .andExpect(jsonPath("$.authors[0].email.email", is("author@gmail.com")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void getNotFoundExceptionInBookIdEndpoint() throws Exception {
        mockMvc.perform(get("/library/book/getBookById/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}