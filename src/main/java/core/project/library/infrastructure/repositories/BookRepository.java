package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.sql_mappers.RowToBook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BookRepository {

    private final Optional<JdbcTemplate> jdbcTemplate;

    private final Optional<RowToBook> rowToBook;

    public BookRepository(Optional<JdbcTemplate> jdbcTemplate, Optional<RowToBook> rowToBook) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToBook = rowToBook;
    }

    public Optional<Book> getBookById(String bookId) {
        if (jdbcTemplate.isEmpty()) {
            throw new RuntimeException("JdbcTemplate dependency doesn`t exists in BookRepository class.");
        }

        return Optional.ofNullable(jdbcTemplate.get()
                .queryForObject("Select * from Book where id=?", rowToBook.orElseThrow(), bookId)
        );
    }
}
