package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.mappers.sql_mappers.RowToBook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BookRepository {

    private final Optional<JdbcTemplate> jdbcTemplate;

    public BookRepository(Optional<JdbcTemplate> jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Book> getBookById(String bookId) {
        if (jdbcTemplate.isEmpty()) {
            throw new RuntimeException("JdbcTemplate dependency doesn`t exists in BookRepository class.");
        }

        return Optional.ofNullable(jdbcTemplate.get()
                .queryForObject(
                        "Select * from Book where id=?", new RowToBook(), bookId)
                );
    }
}
