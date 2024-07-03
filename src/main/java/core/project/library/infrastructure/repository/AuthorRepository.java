package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.utilities.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class AuthorRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean emailExists(Email verifiableEmail) {
        String findEmail = "SELECT COUNT(*) FROM Authors WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(
                findEmail,
                Integer.class,
                verifiableEmail.email());
        return count != null && count > 0;
    }

    public Result<Author, EmptyResultDataAccessException> findById(UUID authorId) {
        try {
            String findById = "SELECT * FROM Authors WHERE id = ?";

            return Result.success(
                    jdbcTemplate.queryForObject(findById, this::authorMapper, authorId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Result.failure(e);
        }
    }

    public Result<List<Author>, Exception> findByLastName(String lastName) {
        try {
            String findByLastName = "SELECT * FROM Authors WHERE last_name = ?";

            List<Author> authors = jdbcTemplate.query(
                    findByLastName, this::authorMapper, lastName
            );

            if (authors.isEmpty()) {
                return Result.failure(new NotFoundException("Authors not found"));
            } else {
                return Result.success(authors);
            }
        } catch (EmptyResultDataAccessException e) {
            return Result.failure(e);
        }
    }

    @Transactional
    public Result<Author, Exception> saveAuthor(Author author) {
        try {
            String saveAuthor = """
                    INSERT INTO Authors (id, first_name, last_name, email,
                                state, city, street, home, creation_date, last_modified_date)
                                VALUES (?,?,?,?,?,?,?,?,?,?)
                    """;

            jdbcTemplate.update(saveAuthor,
                    author.getId().toString(), author.getFirstName().firstName(), author.getLastName().lastName(),
                    author.getEmail().email(), author.getAddress().state(), author.getAddress().city(),
                    author.getAddress().street(), author.getAddress().home(),
                    author.getEvents().creation_date(), author.getEvents().last_update_date()
            );

            return Result.success(author);
        } catch (DataAccessException e) {
            return Result.failure(e);
        }
    }

    private Author authorMapper(ResultSet rs, int rowNum) throws SQLException {
        Address address = new Address(
                rs.getString("state"),
                rs.getString("city"),
                rs.getString("street"),
                rs.getString("home")
        );

        Events events = new Events(
                rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
        );

        return Author.builder()
                .id(UUID.fromString(rs.getString("id")))
                .firstName(new FirstName(rs.getString("first_name")))
                .lastName(new LastName(rs.getString("last_name")))
                .email(new Email(rs.getString("email")))
                .address(address)
                .events(events)
                .build();
    }
}
