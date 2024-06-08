package core.project.library.application.controllers;

import core.project.library.application.service.BookService;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.project.library.infrastructure.utilities.ValueObjects.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BookController.class)
class BookControllerTest {

    private static final Faker faker = new Faker();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookService service;
    @MockBean
    BookRepository repository;

    private static Supplier<Book> getBookSupplier() {
        Supplier<Publisher> publisherSupplier = getPublisherSupplier();
        Supplier<Set<Author>> authorSupplier = getAuthorSupplier();

        return () -> Book.builder()
                .id(UUID.randomUUID())
                .title(randomTitle())
                .description(randomDescription())
                .isbn(randomISBN13())
                .price(BigDecimal.ONE)
                .quantityOnHand(1)
                .events(new Events())
                .category(randomCategory())
                .publisher(publisherSupplier.get())
                .authors(authorSupplier.get())
                .build();
    }

    private static Supplier<Set<Author>> getAuthorSupplier() {
        int random = ThreadLocalRandom.current().nextInt(1, 4);
        return () -> Stream.generate(() -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(randomFirstName())
                .lastName(randomLastName())
                .email(randomEmail())
                .address(randomAddress())
                .events(new Events())
                .build()).limit(random).collect(Collectors.toSet());
    }

    private static Supplier<Publisher> getPublisherSupplier() {
        return () -> Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(randomPublisherName())
                .address(randomAddress())
                .phone(randomPhone())
                .email(randomEmail())
                .events(new Events())
                .build();
    }

    @Nested
    @DisplayName("GetBookById endpoint")
    class FindById {

        private static final String FIND_BY_ID = "/library/book/findById/";

        private static Stream<Arguments> randomBook() {
            Supplier<Book> bookSupplier = getBookSupplier();
            return Stream.generate(() -> arguments(bookSupplier.get()))
                    .limit(1);
        }

        @ParameterizedTest
        @MethodSource("randomBook")
        @DisplayName("Accept valid UUID")
        void acceptValidId(Book book) throws Exception {
            given(
                    service.findById(any(UUID.class))
            )
                    .willReturn(Optional.of(book));

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
            when(
                    service.findById(nonExistent)
            )
                    .thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get(FIND_BY_ID + nonExistent)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("FindByName endpoint")
    class FindByTitle {

        private static final String FIND_BY_NAME = "/library/book/findByTitle/";

        private static Stream<Arguments> randomBook() {
            Supplier<Book> bookSupplier = getBookSupplier();
            return Stream.generate(() -> arguments(bookSupplier.get()))
                    .limit(1);
        }

        @ParameterizedTest
        @MethodSource("randomBook")
        @DisplayName("Accept existing name")
        void acceptExistingName(Book book) throws Exception {
            String title = book.getTitle().title();
            when(
                    service.findByTitle(title)
            )
                    .thenReturn(Optional.of(book));

            mockMvc.perform(get(FIND_BY_NAME + title)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title.title", is(book.getTitle().title())));
        }

        @Test
        @DisplayName("Throw exception in case of no match")
        void testNoMatch() throws Exception {
            String title = "title";
            when(
                    service.findByTitle(title)
            )
                    .thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get(FIND_BY_NAME + title)
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
            Supplier<Book> bookSupplier = getBookSupplier();
            int pageSize = ThreadLocalRandom.current().nextInt(1, 11);
            return Stream.generate(() -> arguments(
                    Stream.generate(bookSupplier).limit(pageSize).toList(),
                    pageSize
            )).limit(1);
        }

        private static Stream<Arguments> bookListByCategory() {
            Supplier<Publisher> publisherSupplier = getPublisherSupplier();
            Supplier<Set<Author>> authorSupplier = getAuthorSupplier();

            Supplier<Book> bookSupplier = () ->
                    Book.builder()
                            .id(UUID.randomUUID())
                            .title(randomTitle())
                            .description(randomDescription())
                            .isbn(randomISBN13())
                            .price(BigDecimal.ONE)
                            .quantityOnHand(1)
                            .events(new Events())
                            .category(randomCategory())
                            .publisher(publisherSupplier.get())
                            .authors(authorSupplier.get())
                            .build();

            int pageSize = ThreadLocalRandom.current().nextInt(1, 11);
            return Stream.generate(() -> arguments(
                    Stream.generate(bookSupplier).limit(10).toList(),
                    randomCategory().name(),
                    pageSize
            )).limit(1);
        }

        private static Stream<Arguments> bookListByAuthor() {
            Supplier<Publisher> publisherSupplier = getPublisherSupplier();

            String name = faker.name().firstName();
            Supplier<Set<Author>> authorSupplier = () -> {
                int numberOfAuthors = ThreadLocalRandom.current().nextInt(1, 4);
                return Stream.generate(() -> Author.builder()
                        .id(UUID.randomUUID())
                        .firstName(new FirstName(name))
                        .lastName(randomLastName())
                        .email(randomEmail())
                        .address(randomAddress())
                        .events(new Events())
                        .build()).limit(numberOfAuthors).collect(Collectors.toSet());
            };

            Supplier<Book> bookSupplier = () -> Book.builder()
                    .id(UUID.randomUUID())
                    .title(randomTitle())
                    .description(randomDescription())
                    .isbn(randomISBN13())
                    .price(BigDecimal.ONE)
                    .quantityOnHand(1)
                    .events(new Events())
                    .category(randomCategory())
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

        @ParameterizedTest
        @MethodSource("bookList")
        @DisplayName("Get list of books")
        void getListOfBooks(List<Book> books, int pageSize) throws Exception {
            when(
                    service.listOfBooks(
                            0, pageSize, null, null)
            )
                    .thenReturn(Optional.of(books));

            mockMvc.perform(get("/library/book/pageOfBook?pageNumber=0&pageSize=%s"
                            .formatted(pageSize))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

        }

        @ParameterizedTest
        @MethodSource("bookListByCategory")
        @DisplayName("Get list of books sorted by category")
        void getListOfBookSortedByCategory(List<Book> books,
                                           String category, int pageSize) throws Exception {
            when(
                    service.listOfBooks(
                            0, pageSize, category, null)
            )
                    .thenReturn(Optional.of(books));

            mockMvc.perform(get("/library/book/pageOfBook?pageNumber=0&pageSize=%s&category=%s"
                            .formatted(pageSize, category))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Reject invalid category")
        void rejectInvalidCategory() throws Exception {
            when(
                    service.listOfBooks(
                            0, 10, "invalid", null)
            )
                    .thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(
                            get("/library/book/pageOfBook?pageNumber=0&pageSize=10&category=invalid")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }

        @ParameterizedTest
        @MethodSource("bookListByAuthor")
        @DisplayName("Get list of books sorted by author")
        void getListOfBooksSortedByAuthor(List<Book> books,
                                          String author, int pageSize) throws Exception {
            given(
                    service.listOfBooks(
                            0, pageSize, null, author
                    )
            )
                    .willReturn(Optional.of(books));

            mockMvc.perform(
                            get("/library/book/pageOfBook?pageNumber=0&pageSize=%s&author=%s"
                                    .formatted(pageSize, author))
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").isNotEmpty())
                    .andReturn();
        }

        @Test
        @DisplayName("Reject invalid author")
        void rejectInvalidAuthor() throws Exception {
            String category = "Adventure";
            when(
                    service.listOfBooks(
                            0, 10, category, "invalid"
                    )
            )
                    .thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(
                            get("/library/book/pageOfBook?pageNumber=0&pageSize=10&author=invalid")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }
}