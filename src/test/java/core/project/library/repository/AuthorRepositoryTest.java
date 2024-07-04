package core.project.library.repository;

import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.repository.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@JdbcTest
class AuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @TestConfiguration
    static class TestConfig {
        @Bean
        AuthorRepository authorRepository(JdbcClient jdbcClient) {
            return new AuthorRepository(jdbcClient);
        }
    }

    static Stream<Arguments> author() {
        Supplier<Author> author =() -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(Bootstrap.randomFirstName())
                .lastName(Bootstrap.randomLastName())
                .address(Bootstrap.randomAddress())
                .email(Bootstrap.randomEmail())
                .events(new Events())
                .build();

        return Stream.generate(() -> arguments(author.get())).limit(1);
    }


    @ParameterizedTest
    @MethodSource("author")
    @Order(1)
    @DisplayName("Save valid author")
    void saveAuthor(Author author) {
        assertThat(authorRepository.saveAuthor(author).value())
                .isNotNull()
                .isEqualTo(author);
    }

    @ParameterizedTest
    @MethodSource("author")
    @DisplayName("reject invalid author")
    void rejectInvalidAuthor(Author author) {
        when(jdbcTemplate.update(any(PreparedStatementCreator.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThat(authorRepository.saveAuthor(author).throwable())
                .isNotNull()
                .isInstanceOf(DataAccessException.class);
    }

    @ParameterizedTest
    @MethodSource("author")
    @DisplayName("FindById test")
    void findById(Author author) {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any())).thenReturn(List.of(author));

        assertThat(authorRepository.findById(author.getId()).value())
                .isNotNull()
                .isEqualTo(author);
    }

    @Test
    @DisplayName("reject invalid id")
    void rejectInvalidId() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any()))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThat(authorRepository.findById(UUID.randomUUID()).throwable())
                .isNotNull()
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    private static Stream<Arguments> authorName() {
        String lastName = "Williams";

        Supplier<Author> author =() -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(Bootstrap.randomFirstName())
                .lastName(new LastName(lastName))
                .email(Bootstrap.randomEmail())
                .address(Bootstrap.randomAddress())
                .events(new Events())
                .build();

        List<Author> authors = Stream.generate(author).limit(5).toList();

        return Stream.generate(() -> arguments(authors, lastName)).limit(1);
    }

    @ParameterizedTest
    @MethodSource("authorName")
    @DisplayName("FindByLastName test")
    void findByLastName(List<Author> authors, String lastName) {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any())).thenReturn(authors);

        assertThat(authorRepository.findByLastName(lastName).value())
                .allMatch(author -> author.getLastName().lastName().equals(lastName));
    }
}
