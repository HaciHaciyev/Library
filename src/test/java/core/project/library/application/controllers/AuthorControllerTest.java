package core.project.library.application.controllers;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.AuthorRepository;
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

import static core.project.library.infrastructure.utilities.ValueObjects.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    private static final Faker faker = new Faker();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorRepository repository;

    @Nested
    @DisplayName("FindById endpoint")
    class FindByIdEndpoint{

        @ParameterizedTest
        @MethodSource("author")
        @DisplayName("Accept valid UUID")
        void acceptValidUuid(Author author) throws Exception {
            when(repository.findById(author.getId())).thenReturn(Optional.of(author));

            mockMvc.perform(get("/library/author/findById/" + author.getId())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.firstName.firstName", is(author.getFirstName().firstName())),
                            jsonPath("$.lastName.lastName", is(author.getLastName().lastName())),
                            jsonPath("$.email.email", is(author.getEmail().email())),
                            jsonPath("$.address.state", is(author.getAddress().state())),
                            jsonPath("$.address.city", is(author.getAddress().city())),
                            jsonPath("$.address.street", is(author.getAddress().street())),
                            jsonPath("$.address.home", is(author.getAddress().home()))
                    );
        }

        @Test
        @DisplayName("Reject invalid UUID")
        void rejectInvalidId() throws Exception {
            UUID invalid = UUID.randomUUID();
            when(repository.findById(invalid)).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get("/library/author/findById/" + invalid)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }

        private static Stream<Arguments> author() {
            Supplier<Author> authorSupplier = () -> Author.builder()
                    .id(UUID.randomUUID())
                    .firstName(randomFirstName())
                    .lastName(randomLastName())
                    .email(randomEmail())
                    .address(randomAddress())
                    .events(new Events())
                    .build();

            return Stream.generate(() -> arguments(authorSupplier.get()))
                    .limit(1);
        }
    }

    @Nested
    @DisplayName("FindByLastName endpoint")
    class FindByLastName {

        @ParameterizedTest
        @MethodSource("authors")
        @DisplayName("Return matching authors")
        void returnMatchingAuthors(List<Author> authors, String lastName) throws Exception {
            when(repository.findByLastName(lastName)).thenReturn(Optional.of(authors));

            mockMvc.perform(get("/library/author/findByLastName/" + lastName)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Reject invalid name")
        void rejectInvalidName() throws Exception {
            String invalid = "invalid";
            when(repository.findByLastName(invalid)).thenReturn(Optional.empty());

            mockMvc.perform(get("/library/author/findByLastName/" + invalid)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        private static Stream<Arguments> authors() {
            String lastName = faker.name().lastName();
            Supplier<Author> authorSupplier = () -> Author.builder()
                    .id(UUID.randomUUID())
                    .firstName(randomFirstName())
                    .lastName(new LastName(lastName))
                    .email(randomEmail())
                    .address(randomAddress())
                    .events(new Events())
                    .build();

            List<Author> authors = Stream.generate(authorSupplier).limit(5).toList();
            return Stream.generate(() -> arguments(authors, lastName)).limit(1);
        }
    }
}