package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
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

    public List<Optional<Author>> getAuthorsByBookId(UUID bookId) {
        List<String> uuids = jdbcTemplate.queryForList("Select author_id from Book_Author where book_id=?",
                String.class, bookId.toString());

        List<Optional<Author>> authors = new ArrayList<>();
        uuids.forEach(uuid -> authors.add(Optional.ofNullable(
                jdbcTemplate.queryForObject("Select * from Author where id=?", rowToAuthor, uuid))));

        return authors;
    }

    public Optional<Author> saveAuthor(Author author) {
        Author authorForSave = Author.builder()
                .id(UUID.randomUUID())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .email(author.getEmail())
                .address(author.getAddress())
                .events(new Events())
                .build();

        jdbcTemplate.update("""
        Insert into Author (id, first_name, last_name, email,
                    state, city, street, home, created_date, last_modified_date)
                    values (?,?,?,?,?,?,?,?,?,?)
        """,
                authorForSave.getId().toString(), authorForSave.getFirstName().firstName(),
                authorForSave.getLastName().lastName(), authorForSave.getEmail().email(),
                authorForSave.getAddress().state(), authorForSave.getAddress().city(),
                authorForSave.getAddress().street(), authorForSave.getAddress().home(),
                authorForSave.getEvents().creation_date(), authorForSave.getEvents().last_update_date()
        );

        return Optional.of(authorForSave);
    }

}
