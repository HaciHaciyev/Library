package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.utilities.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class AuthorRepository {

    private final JdbcClient jdbcClient;

    public AuthorRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public boolean emailExists(Email verifiableEmail) {
        String findEmail = "SELECT COUNT(*) FROM Authors WHERE email = ?";

        Integer count = jdbcClient.sql(findEmail)
                .param(verifiableEmail.email())
                .query(Integer.class)
                .single();

        return count > 0;
    }

    public Result<Author, DataAccessException> findById(UUID authorId) {
        try {
            String findById = "SELECT * FROM Authors WHERE id = ?";

            Author author = jdbcClient.sql(findById)
                    .param(authorId.toString())
                    .query(this::authorMapper)
                    .single();

            return Result.success(author);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(e);
        }
    }

    public Result<List<Author>, Exception> findByLastName(String lastName) {
        try {
            String findByLastName = "SELECT * FROM Authors WHERE last_name = ?";

            List<Author> authors = jdbcClient.sql(findByLastName)
                    .param(lastName)
                    .query(this::authorMapper)
                    .list();

            if (authors.isEmpty()) {
                log.error("No authors found for last name {}", lastName);
                return Result.failure(new NotFoundException("Authors not found"));
            }

            return Result.success(authors);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(e);
        }
    }

    @Transactional
    public Result<Author, DataAccessException> saveAuthor(Author author) {
        try {
            String saveAuthor = """
                    INSERT INTO Authors (id, first_name, last_name, email,
                                state, city, street, home, creation_date, last_modified_date)
                                VALUES (?,?,?,?,?,?,?,?,?,?)
                    """;

            jdbcClient.sql(saveAuthor)
                    .param(author.getId().toString())
                    .param(author.getFirstName().firstName())
                    .param(author.getLastName().lastName())
                    .param(author.getEmail().email())
                    .param(author.getAddress().state())
                    .param(author.getAddress().city())
                    .param(author.getAddress().street())
                    .param(author.getAddress().home())
                    .param(author.getEvents().creation_date())
                    .param(author.getEvents().last_update_date())
                    .update();

            return Result.success(author);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
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
