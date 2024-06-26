package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import core.project.library.infrastructure.exceptions.Result;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
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

    public Result<Publisher, Exception> findById(UUID publisherId) {
        try {
            String findById = "SELECT * FROM Publishers WHERE id = ?";

            return Result.success(
                    jdbcTemplate.queryForObject(findById, this::publisherMapper, publisherId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Result.failure(e);
        }
    }

    public Result<List<Publisher>, Exception> findByName(String publisherName) {
        try {
            String findByName = "SELECT * FROM Publishers WHERE publisher_name = ?";

            return Result.success(
                    jdbcTemplate.query(findByName, this::publisherMapper, publisherName)
            );
        } catch (EmptyResultDataAccessException e) {
            return Result.failure(e);
        }
    }

    public Result<Publisher, Exception> savePublisher(Publisher publisher) {
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

        return Result.success(publisher);
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

        return Publisher.builder()
                .id(UUID.fromString(rs.getString("id")))
                .publisherName(new PublisherName(rs.getString("publisher_name")))
                .address(address)
                .phone(new Phone(rs.getString("phone")))
                .email(new Email(rs.getString("email")))
                .events(events)
                .build();
    }
}
