package core.project.library.repository;

import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.repository.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

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
        AuthorRepository authorRepository(JdbcTemplate jdbcTemplate) {
            return new AuthorRepository(jdbcTemplate);
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

        return Stream.generate(() -> arguments(author.get())).limit(5);
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
        when(jdbcTemplate.update(anyString(), any(PreparedStatementSetter.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThat(authorRepository.saveAuthor(author).throwable())
                .isNotNull()
                .isInstanceOf(DataAccessException.class);
    }
}
