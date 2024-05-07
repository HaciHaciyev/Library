package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Author;
import core.project.library.infrastructure.repositories.sql_mappers.RowToAuthor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthorRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowToAuthor rowToAuthor;

    public AuthorRepository(JdbcTemplate jdbcTemplate, RowToAuthor rowToAuthor) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToAuthor = rowToAuthor;
    }

    public List<Optional<Author>> getAuthorsByBookId(String bookId) {
        List<UUID> uuids = jdbcTemplate.queryForList("Select author_id from Book_Author where book_id=?",
                UUID.class, bookId);

        List<Optional<Author>> authors = new ArrayList<>();
        uuids.forEach(uuid -> authors.add(Optional.ofNullable(
                jdbcTemplate.queryForObject("Select * from Author where id=?", rowToAuthor, uuid))));

        return authors;
    }
}
