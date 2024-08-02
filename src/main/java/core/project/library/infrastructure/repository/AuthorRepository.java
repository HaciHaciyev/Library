package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public Optional<Author> findById(UUID authorId) {
        try {
            String findById = "SELECT * FROM Authors WHERE id = ?";

            Author author = jdbcClient.sql(findById)
                    .param(authorId.toString())
                    .query(this::authorMapper)
                    .single();

            return Optional.of(author);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public List<Author> findByLastName(String lastName) {
        try {
            String findByLastName = "SELECT * FROM Authors WHERE last_name = ?";

            return jdbcClient.sql(findByLastName)
                    .param(lastName)
                    .query(this::authorMapper)
                    .list();

        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public Optional<Author> saveAuthor(Author author) {
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

            return Optional.of(author);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Optional.empty();
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

        return Author.create(
                UUID.fromString(rs.getString("id")),
                new FirstName(rs.getString("first_name")),
                new LastName(rs.getString("last_name")),
                new Email(rs.getString("email")),
                address,
                events
        );
    }
}
