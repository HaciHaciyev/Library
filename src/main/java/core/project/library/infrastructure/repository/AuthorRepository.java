package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthorRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isEmailExists(Email verifiableEmail) {
        try {
            Email email = jdbcTemplate.queryForObject(
                    sqlForEmail,
                    (rs, rowNum) -> new Email(rs.getString("email")),
                    verifiableEmail.email()
            );
            return email == null && email != verifiableEmail;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public void saveAuthor(Author author) {
        jdbcTemplate.update("""
                        Insert into Authors (id, first_name, last_name, email,
                                    state, city, street, home, creation_date, last_modified_date)
                                    values (?,?,?,?,?,?,?,?,?,?)
                        """,
                author.getId().toString(), author.getFirstName().firstName(), author.getLastName().lastName(),
                author.getEmail().email(), author.getAddress().state(), author.getAddress().city(),
                author.getAddress().street(), author.getAddress().home(),
                author.getEvents().creation_date(), author.getEvents().last_update_date()
        );
    }

    public Optional<Author> findById(UUID authorId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlForGetAuthor, new RowToAuthor(), authorId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Author>> findByLastName(String lastName) {
        try {
            return Optional.of(
                    jdbcTemplate.query(sqlForAuthorByLastName, new RowToAuthor(), lastName)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static final String sqlForGetAuthor = """
            Select * from Authors Where id = ?
            """;

    private static final String sqlForAuthorByLastName = """
            Select * from Authors Where last_name = ?
            """;

    private static final String sqlForEmail = """
            Select email from Authors Where email = ?
            """;

    private static final class RowToAuthor implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Author.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .firstName(new FirstName(rs.getString("first_name")))
                    .lastName(new LastName(rs.getString("last_name")))
                    .email(new Email(rs.getString("email")))
                    .address(new Address(
                            rs.getString("state"),
                            rs.getString("city"),
                            rs.getString("street"),
                            rs.getString("home")
                    ))
                    .events(new Events(
                            rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                            rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                            )
                    )
                    .build();
        }
    }
}
