package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.application.model.AuthorDTO;
import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.mappers.AuthorMapper;
import core.project.library.infrastructure.repository.AuthorRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    private static final Faker faker = new Faker();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorRepository mockRepo;

    @MockBean
    AuthorMapper mockMapper;

    @Autowired
    ObjectMapper objectMapper;


    @Nested
    @DisplayName("FindById endpoint")
    class FindByIdEndpoint {

        public static final String FIND_BY_ID = "/library/author/findById/";

        private static Stream<Arguments> dtoEntity() {
            Author entity = Bootstrap.authorFactory().get();

            AuthorDTO authorDTO = new AuthorDTO(
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getAddress()
            );

            return Stream.generate(() -> arguments(authorDTO, entity)).limit(1);
        }

        @ParameterizedTest
        @MethodSource("dtoEntity")
        @DisplayName("Accept valid UUID")
        void acceptValidUuid(AuthorDTO authorDTO, Author author) throws Exception {
            when(mockRepo.findById(author.getId())).thenReturn(Optional.of(author));
            when(mockMapper.toDTO(author)).thenReturn(authorDTO);

            mockMvc.perform(get(FIND_BY_ID + author.getId())
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
            when(mockRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

            mockMvc.perform(get(FIND_BY_ID + UUID.randomUUID())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Author's not found");
                    });
        }
    }

    @Nested
    @DisplayName("FindByLastName endpoint")
    class FindByLastName {

        public static final String FIND_BY_LAST_NAME = "/library/author/findByLastName/";

        private static Stream<Arguments> authorsDtosName() {
            String lastName = faker.name().lastName();

            Supplier<Author> authorSupplier = () -> Author.create(
                    UUID.randomUUID(),
                    Bootstrap.randomFirstName(),
                    new LastName(lastName),
                    Bootstrap.randomEmail(),
                    Bootstrap.randomAddress(),
                    new Events()
            );

            List<Author> authors = Stream.generate(authorSupplier).limit(5).toList();

            List<AuthorDTO> dtos = authors.stream()
                    .map(author -> new AuthorDTO(
                            author.getFirstName(),
                            author.getLastName(),
                            author.getEmail(),
                            author.getAddress()))
                    .toList();

            return Stream.generate(() -> arguments(authors, dtos, lastName)).limit(1);
        }

        @ParameterizedTest
        @MethodSource("authorsDtosName")
        @DisplayName("Return matching authors")
        void returnMatchingAuthors(List<Author> authors, List<AuthorDTO> authorDTOS, String lastName) throws Exception {
            when(mockRepo.findByLastName(lastName)).thenReturn(authors);
            when(mockMapper.listOfDTO(authors)).thenReturn(authorDTOS);

            mockMvc.perform(get(FIND_BY_LAST_NAME + lastName)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[*].lastName.lastName", everyItem(is(lastName))));
        }

        @Test
        @DisplayName("Reject invalid name")
        void rejectInvalidName() throws Exception {
            String invalid = "invalid";
            when(mockRepo.findByLastName(invalid)).thenReturn(Collections.emptyList());

            mockMvc.perform(get(FIND_BY_LAST_NAME + invalid)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Author's not found");
                    });
        }
    }

    @Nested
    @DisplayName("Save author endpoint")
    class SaveAuthorEndpoint {

        public static final String SAVE_AUTHOR = "/library/author/saveAuthor";

        private static Stream<Arguments> authorDTO() {
            AuthorDTO authorDTO = new AuthorDTO(Bootstrap.randomFirstName(),
                    Bootstrap.randomLastName(),
                    Bootstrap.randomEmail(),
                    Bootstrap.randomAddress());

            Supplier<Author> supplier = Bootstrap.authorFactory();
            return Stream.generate(() -> arguments(authorDTO, supplier.get())).limit(1);
        }

        @ParameterizedTest
        @MethodSource("authorDTO")
        @DisplayName("accept valid DTO")
        void acceptValidDTO(AuthorDTO authorDTO, Author author) throws Exception {
            when(mockRepo.saveAuthor(author)).thenReturn(Optional.of(author));
            when(mockMapper.authorFromDTO(authorDTO)).thenReturn(author);

            mockMvc.perform(post(SAVE_AUTHOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authorDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully saved author"))
                    .andExpect(header().exists("Location"));
        }

        @ParameterizedTest
        @MethodSource("authorDTO")
        @DisplayName("reject DTO with existing email")
        void rejectDTOWithExistingEmail(AuthorDTO authorDTO, Author author) throws Exception {
            when(mockRepo.emailExists(any())).thenReturn(true);
            when(mockMapper.authorFromDTO(authorDTO)).thenReturn(author);

            mockMvc.perform(post(SAVE_AUTHOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authorDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Email already exists");
                    });
        }
    }
}