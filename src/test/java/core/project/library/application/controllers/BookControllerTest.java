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
import core.project.library.infrastructure.repositories.OrderRepository;
import core.project.library.infrastructure.repositories.PublisherRepository;
import core.project.library.infrastructure.repositories.sql_mappers.RowToAuthor;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import core.project.library.infrastructure.repositories.sql_mappers.RowToPublisher;
import core.project.library.infrastructure.services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
class BookControllerTest {

    MockMvc mockMvc;
    @Autowired
    BookController bookController;
    @Autowired
    Optional<EntityMapper> entityMapper;
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
    void getBookByIdTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/getBookById/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("Title")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("978-161-729-045-9")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(43)))
                .andExpect(jsonPath("$.publisher.publisherName.publisherName", is("Publisher")))
                .andExpect(jsonPath("$.authors[0].firstName.firstName", is("Author")))
                .andExpect(jsonPath("$.authors[0].email.email", is("author@gmail.com")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getNotFoundExceptionInBookIdEndpoint() throws Exception {
        mockMvc.perform(get("/library/book/getBookById/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByTitle() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/findByName/Title")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("Title")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("978-161-729-045-9")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(43)))
                .andExpect(jsonPath("$.publisher.publisherName.publisherName", is("Publisher")))
                .andExpect(jsonPath("$.authors[0].firstName.firstName", is("Author")))
                .andExpect(jsonPath("$.authors[0].email.email", is("author@gmail.com")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getNotFoundExceptionInBookTitleEndpoint() throws Exception {
        mockMvc.perform(get("/library/book/findByName/Doesn`t_Exists")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveBook() throws Exception {
        BookModel book = entityMapper.get().toModel(getBookForTest());

        mockMvc.perform(post("/library/book/saveBook")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    private Book getBookForTest() {
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Test Publisher"))
                .address(new Address("State", "City", "Street", "Home"))
                .phone(new Phone("11122-333-44-55"))
                .email(new Email("email@gmail.com"))
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .books(new HashSet<>())
                .build();

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Test Author"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .books(new HashSet<>())
                .build();

        Book book = Book.builder()
                .id(UUID.fromString("d4f0aa27-317b-4e00-9462-9a7f0faa7a5e"))
                .title(new Title("Test Title"))
                .description(new Description("Description"))
                .isbn(new ISBN("978-161-729-045-9"))
                .price(new BigDecimal("12.99"))
                .quantityOnHand(43)
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .category(Category.Adventure)
                .authors(new HashSet<>())
                .orders(new HashSet<>())
                .build();

        book.addAuthor(author);
        book.addPublisher(publisher);

        return book;
    }
}