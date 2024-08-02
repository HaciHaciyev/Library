package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PublisherRepository {

    private final JdbcTemplate jdbcTemplate;

    public PublisherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean emailExists(Email email) {
        String findEmail = "SELECT COUNT(*) FROM Publishers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(
                findEmail,
                Integer.class,
                email.email()
        );
        return count != null && count > 0;
    }

    public boolean phoneExists(Phone phone) {
        String findPhone = "SELECT COUNT(*) FROM Publishers WHERE phone = ?";
        Integer count = jdbcTemplate.queryForObject(
                findPhone,
                Integer.class,
                phone.phoneNumber()
        );
        return count != null && count > 0;
    }

    public Optional<Publisher> findById(UUID publisherId) {
        try {
            String findById = "SELECT * FROM Publishers WHERE id = ?";

            return Optional.of(
                    jdbcTemplate.queryForObject(findById, this::publisherMapper, publisherId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Publisher> findByName(String publisherName) {
        try {
            String findByName = "SELECT * FROM Publishers WHERE publisher_name = ?";

            return jdbcTemplate.query(findByName, this::publisherMapper, publisherName);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public Optional<Publisher> savePublisher(Publisher publisher) {
        String savePublisher = """
                INSERT INTO Publishers (id, publisher_name, state, city, street, home,
                               phone, email, creation_date, last_modified_date)
                               VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        jdbcTemplate.update(savePublisher,
                publisher.getId().toString(), publisher.getPublisherName().publisherName(),
                publisher.getAddress().state(), publisher.getAddress().city(),
                publisher.getAddress().street(), publisher.getAddress().home(),
                publisher.getPhone().phoneNumber(), publisher.getEmail().email(),
                publisher.getEvents().creation_date(), publisher.getEvents().last_update_date()
        );

        return Optional.of(publisher);
    }

    private Publisher publisherMapper(ResultSet rs, int rowNum) throws SQLException {
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

        return Publisher.create(
                UUID.fromString(rs.getString("id")),
                new PublisherName(rs.getString("publisher_name")),
                address,
                new Phone(rs.getString("phone")),
                new Email(rs.getString("email")),
                events
        );
    }
}
