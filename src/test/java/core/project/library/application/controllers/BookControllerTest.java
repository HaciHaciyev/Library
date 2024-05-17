package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.BookModel;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.repositories.AuthorRepository;
import core.project.library.infrastructure.repositories.BootstrapRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import core.project.library.infrastructure.repositories.PublisherRepository;
import core.project.library.infrastructure.repositories.sql_mappers.RowToAuthor;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import core.project.library.infrastructure.repositories.sql_mappers.RowToPublisher;
import core.project.library.infrastructure.services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    BootstrapRepository bootstrapRepository;
    @Autowired
    PublisherRepository publisherRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RowToAuthor rowToAuthor;
    @Autowired
    RowToBook rowToBook;
    @Autowired
    RowToPublisher rowToPublisher;
    @Autowired
    RowToOrder rowToOrder;
    @Autowired
    BookService bookService;
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
                        get("/library/book/getBookById/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
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

    @Test
    @Order(3)
    void getByTitle() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/findByName/Title")
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
    @Order(4)
    void getNotFoundExceptionInBookTitleEndpoint() throws Exception {
        mockMvc.perform(get("/library/book/findByName/Doesn`t_Exists")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    void saveBook() throws Exception {
        BookModel bookModel = entityMapper.get().toModel(getBookForTest());

        mockMvc.perform(post("/library/book/saveBook")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookModel)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @Order(6)
    void updateBook() throws Exception {
        BookModel bookModel = entityMapper.get().toModel(getBookForTest());

        MvcResult mvcResult = mockMvc.perform(put("/library/book/updateBook/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookModel)))
                .andExpect(status().isAccepted())
                .andExpect(header().exists("Location"))
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

    private Book getBookForTest() {
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Test Publisher"))
                .address(new Address("State", "City", "Street", "Home"))
                .phone(new Phone("+994 50 1112233"))
                .email(new Email("email@gmail.com"))
                .events(new Events())
                .build();

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Test Author"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events())
                .build();

        Book book = Book.builder()
                .id(UUID.fromString("d4f0aa27-317b-4e00-9462-9a7f0faa7a5e"))
                .title(new Title("Title"))
                .description(new Description("Description"))
                .isbn(new ISBN("9781861972712"))
                .price(new BigDecimal("12.99"))
                .quantityOnHand(43)
                .events(new Events())
                .category(Category.Adventure)
                .build();

        book.addAuthor(author);
        book.addPublisher(publisher);

        return book;
    }
}