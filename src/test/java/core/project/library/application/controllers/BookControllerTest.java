package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.application.model.BookDTO;
import core.project.library.application.service.BookService;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.AuthorRepository;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.PublisherRepository;
import core.project.library.infrastructure.utilities.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BookController.class)
@Disabled("temporarily disabled due to bookRepo being unfinished")
class BookControllerTest {

    private static final Faker faker = new Faker();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookService bookService;
    @MockBean
    BookRepository bookRepository;
    @MockBean
    AuthorRepository authorRepository;
    @MockBean
    PublisherRepository publisherRepository;

    private static Supplier<Book> bookSupplier() {
        Supplier<Publisher> publisherSupplier = publisherSupplier();
        Supplier<Set<Author>> authorSupplier = authorSupplier();

        return () -> Book.builder()
                .id(UUID.randomUUID())
                .title(Bootstrap.randomTitle())
                .description(Bootstrap.randomDescription())
                .isbn(Bootstrap.randomISBN13())
                .price(new Price(0.0))
                .quantityOnHand(new QuantityOnHand(1))
                .events(new Events())
                .category(Bootstrap.randomCategory())
                .publisher(publisherSupplier.get())
                .authors(authorSupplier.get())
                .build();
    }

    private static Supplier<Set<Author>> authorSupplier() {
        int random = ThreadLocalRandom.current().nextInt(1, 4);
        return () -> Stream.generate(() -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(Bootstrap.randomFirstName())
                .lastName(Bootstrap.randomLastName())
                .email(Bootstrap.randomEmail())
                .address(Bootstrap.randomAddress())
                .events(new Events())
                .build()).limit(random).collect(Collectors.toSet());
    }

    private static Supplier<Publisher> publisherSupplier() {
        return () -> Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(Bootstrap.randomPublisherName())
                .address(Bootstrap.randomAddress())
                .phone(Bootstrap.randomPhone())
                .email(Bootstrap.randomEmail())
                .events(new Events())
                .build();
    }

    @Nested
    @DisplayName("GetBookById endpoint")
    class FindById {

        private static final String FIND_BY_ID = "/library/book/findById/";

        private static Stream<Arguments> randomBook() {
            Supplier<Book> bookSupplier = bookSupplier();
            return Stream.generate(() -> arguments(bookSupplier.get()))
                    .limit(1);
        }

        @ParameterizedTest
        @MethodSource("randomBook")
        @DisplayName("Accept valid UUID")
        void acceptValidId(Book book) throws Exception {
            when(bookService.findById(any(UUID.class)))
                    .thenReturn(Result.success(book));

            MvcResult mvcResult = mockMvc.perform(get(FIND_BY_ID + book.getId().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            log.info(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Reject UUID of non existent book")
        void rejectInvalidId() throws Exception {
            UUID nonExistent = UUID.randomUUID();
            when(bookService.findById(nonExistent))
                    .thenReturn(Result.failure(new NotFoundException()));

            MvcResult mvcResult = mockMvc.perform(get(FIND_BY_ID + nonExistent)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("ListOfBooks endpoint")
    class Page {

        private static Stream<Arguments> bookList() {
            Supplier<Book> bookSupplier = bookSupplier();
            int pageSize = ThreadLocalRandom.current().nextInt(1, 11);
            return Stream.generate(() -> arguments(
                    Stream.generate(bookSupplier).limit(pageSize).toList(),
                    pageSize
            )).limit(1);
        }

        private static Stream<Arguments> bookListByCategory() {
            Supplier<Publisher> publisherSupplier = publisherSupplier();
            Supplier<Set<Author>> authorSupplier = authorSupplier();
            Category category = Bootstrap.randomCategory();

            Supplier<Book> bookSupplier = () ->
                    Book.builder()
                            .id(UUID.randomUUID())
                            .title(Bootstrap.randomTitle())
                            .description(Bootstrap.randomDescription())
                            .isbn(Bootstrap.randomISBN13())
                            .price(Bootstrap.randomPrice())
                            .quantityOnHand(Bootstrap.randomQuantityOnHand())
                            .events(new Events())
                            .category(category)
                            .publisher(publisherSupplier.get())
                            .authors(authorSupplier.get())
                            .build();

            int pageSize = ThreadLocalRandom.current().nextInt(1, 11);
            return Stream.generate(() -> arguments(
                    Stream.generate(bookSupplier).limit(pageSize).toList(),
                    category.name(),
                    pageSize
            )).limit(1);
        }

        private static Stream<Arguments> bookListByAuthor() {
            Supplier<Publisher> publisherSupplier = publisherSupplier();

            String name = faker.name().firstName();
            Supplier<Set<Author>> authorSupplier = () -> {
                int numberOfAuthors = ThreadLocalRandom.current().nextInt(1, 4);
                return Stream.generate(() -> Author.builder()
                        .id(UUID.randomUUID())
                        .firstName(new FirstName(name))
                        .lastName(Bootstrap.randomLastName())
                        .email(Bootstrap.randomEmail())
                        .address(Bootstrap.randomAddress())
                        .events(new Events())
                        .build()).limit(numberOfAuthors).collect(Collectors.toSet());
            };

            Supplier<Book> bookSupplier = () -> Book.builder()
                    .id(UUID.randomUUID())
                    .title(Bootstrap.randomTitle())
                    .description(Bootstrap.randomDescription())
                    .isbn(Bootstrap.randomISBN13())
                    .price(Bootstrap.randomPrice())
                    .quantityOnHand(Bootstrap.randomQuantityOnHand())
                    .events(new Events())
                    .category(Bootstrap.randomCategory())
                    .publisher(publisherSupplier.get())
                    .authors(authorSupplier.get())
                    .build();

            int pageSize = ThreadLocalRandom.current().nextInt(1, 11);
            return Stream.generate(() -> arguments(
                    Stream.generate(bookSupplier).limit(10).toList(),
                    name,
                    pageSize
            )).limit(1);
        }

//        @ParameterizedTest
//        @MethodSource("bookList")
//        @DisplayName("Get list of books")
//        void getListOfBooks(List<Book> books, int pageSize) throws Exception {
//            when(bookService.listOfBooks(0, pageSize, null, null))
//                    .thenReturn(books);
//
//            mockMvc.perform(get("/library/book/pageOfBook?pageNumber=0&pageSize=%s"
//                            .formatted(pageSize))
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andReturn();
//        }

//        @ParameterizedTest
//        @MethodSource("bookListByCategory")
//        @DisplayName("Get list of books sorted by category")
//        void getListOfBookSortedByCategory(List<Book> books, String category, int pageSize) throws Exception {
//            when(bookService.listOfBooks(0, pageSize, category, null))
//                    .thenReturn(books);
//            System.out.println(books);
//
//            mockMvc.perform(get("/library/book/pageOfBook?pageNumber=0&pageSize=%s&category=%s"
//                            .formatted(pageSize, category))
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andReturn();
//
//        }

        @Test
        @DisplayName("Reject invalid category")
        void rejectInvalidCategory() throws Exception {
            when(bookService.listOfBooks(0, 10, "invalid", null))
                    .thenThrow(NotFoundException.class);

            MvcResult mvcResult = mockMvc.perform(
                            get("/library/book/pageOfBook?pageNumber=0&pageSize=10&category=invalid")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }

//        @ParameterizedTest
//        @MethodSource("bookListByAuthor")
//        @DisplayName("Get list of books sorted by author")
//        void getListOfBooksSortedByAuthor(List<Book> books,
//                                          String author, int pageSize) throws Exception {
//            when(bookService.listOfBooks(0, pageSize, null, author)).thenReturn(books);
//
//            mockMvc.perform(get("/library/book/pageOfBook?pageNumber=0&pageSize=%s&author=%s"
//                            .formatted(pageSize, author))
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isNotEmpty())
//                    .andReturn();
//        }

        @Test
        @DisplayName("Reject invalid author")
        void rejectInvalidAuthor() throws Exception {
            String category = "Adventure";
            when(bookService.listOfBooks(0, 10, category, "invalid"))
                    .thenThrow(NotFoundException.class);

            MvcResult mvcResult = mockMvc.perform(
                            get("/library/book/pageOfBook?pageNumber=0&pageSize=10&author=invalid")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Save book endpoint")
    class SaveBookEndpoint {

        private static Stream<Arguments> validDTO() {
            BookDTO bookDTO = new BookDTO(Bootstrap.randomTitle(),
                    Bootstrap.randomDescription(),
                    Bootstrap.randomISBN13(),
                    Bootstrap.randomPrice(),
                    Bootstrap.randomQuantityOnHand(),
                    Bootstrap.randomCategory(),
                    false);

            return Stream.generate(() -> arguments(bookDTO)).limit(1);
        }

        private static Stream<Arguments> validDTO_ValidPublisher_ValidAuthors() {
            BookDTO bookDTO = new BookDTO(Bootstrap.randomTitle(),
                    Bootstrap.randomDescription(),
                    Bootstrap.randomISBN13(),
                    Bootstrap.randomPrice(),
                    Bootstrap.randomQuantityOnHand(),
                    Bootstrap.randomCategory(),
                    false);

            List<Author> authors = List.copyOf(authorSupplier().get());

            return Stream.generate(() -> arguments(
                    bookDTO,
                    publisherSupplier().get(),
                    authors
            )).limit(1);
        }

//        @ParameterizedTest
//        @MethodSource("validDTO_ValidPublisher_ValidAuthors")
//        @DisplayName("accept valid bookDTO, publisherId, and authors")
//        void acceptValidDTO(BookDTO dto, Publisher publisher, List<Author> authors) throws Exception {
//            when(bookService.isIsbnExists(dto.isbn())).thenReturn(false);
//            when(publisherRepository.findById(publisher.getId())).thenReturn(Optional.of(publisher));
//            authors.forEach(author -> when(authorRepository.findById(author.getId())).thenReturn(Result.success(author)));
//
//            List<UUID> authorIds = authors.stream().map(Author::getId).toList();
//
//            mockMvc.perform(post("/library/book/saveBook")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto))
//                            .param("publisherId", publisher.getId().toString())
//                            .param("authorsId", authorIds.toString().replaceAll("[\\[\\]]", "")))
//                    .andExpect(status().isCreated())
//                    .andExpect(header().exists("Location"));
//        }

        @ParameterizedTest
        @MethodSource("validDTO")
        @DisplayName("reject invalid bookDTO isbn")
        void rejectInvalidISBN(BookDTO dto) {
            when(bookService.isIsbnExists(any(ISBN.class))).thenReturn(true);

            assertThatThrownBy(() ->
                    mockMvc.perform(post("/library/book/saveBook")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .param("publisherId", UUID.randomUUID().toString())
                            .param("authorsId", UUID.randomUUID().toString())))
                    .hasMessageContaining("ISBN was be used");
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("validDTO")
        @DisplayName("reject when publisher is not found")
        void rejectInvalidPublisher(BookDTO dto) {
            when(bookService.isIsbnExists(any(ISBN.class))).thenReturn(false);

            MvcResult mvcResult = mockMvc.perform(post("/library/book/saveBook")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .param("publisherId", UUID.randomUUID().toString())
                            .param("authorsId", UUID.randomUUID().toString()))
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("validDTO")
        @DisplayName("reject when author/s is not found")
        void rejectInvalidAuthor(BookDTO dto) {
            when(bookService.isIsbnExists(any(ISBN.class))).thenReturn(false);

            MvcResult mvcResult = mockMvc.perform(post("/library/book/saveBook")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .param("publisherId", UUID.randomUUID().toString())
                            .param("authorsId", UUID.randomUUID().toString()))
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }
}