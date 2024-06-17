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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PublisherRepository {

    private static final String GET_BY_ID =
            "Select * from Publishers where id = ?";

    private static final String GET_BY_NAME =
            "Select * from Publishers where publisher_name = ?";

    private static final String FIND_EMAIL =
            "Select email from Publishers where email = ?";

    private static final String FIND_PHONE =
            "Select phone from Publishers where phone = ?";

    private static final String isExists =
            "Select id from Publishers where id = ?";

    private final JdbcTemplate jdbcTemplate;

    public PublisherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isPublisherExists(UUID verifiablePublisherId) {
        try {
             UUID publisherId = jdbcTemplate.queryForObject(
                     isExists,
                     (rs, rowNum) -> UUID.fromString(rs.getString("id")),
                     verifiablePublisherId.toString()
             );
             return publisherId != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean isEmailExists(Email verifiableEmail) {
        try {
            Email email = jdbcTemplate.queryForObject(
                    FIND_EMAIL,
                    (rs, rowNum) -> new Email(rs.getString("email")),
                    verifiableEmail.email()
            );
            return email != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean isPhoneExists(Phone verifiablePhone) {
        try {
            Phone phone = jdbcTemplate.queryForObject(
                    FIND_PHONE,
                    (rs, rowNum) -> new Phone(rs.getString("phone")),
                    verifiablePhone.phoneNumber()
            );
            return phone != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public Optional<Publisher> findById(UUID publisherId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(GET_BY_ID, this::publisherMapper, publisherId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Publisher>> findByName(String name) {
        try {
            return Optional.of(
                    jdbcTemplate.query(GET_BY_NAME, this::publisherMapper, name)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void savePublisher(Publisher publisher) {
        jdbcTemplate.update("""
                        Insert into Publishers (id, publisher_name, state, city, street, home,
                                       phone, email, creation_date, last_modified_date)
                                       values (?,?,?,?,?,?,?,?,?,?)
                        """,
                publisher.getId().toString(), publisher.getPublisherName().publisherName(),
                publisher.getAddress().state(), publisher.getAddress().city(),
                publisher.getAddress().street(), publisher.getAddress().home(),
                publisher.getPhone().phoneNumber(), publisher.getEmail().email(),
                publisher.getEvents().creation_date(), publisher.getEvents().last_update_date()
        );
    }

    private Publisher publisherMapper(ResultSet rs, int ignored) throws SQLException {
        return Publisher.builder()
                .id(UUID.fromString(rs.getString("id")))
                .publisherName(new PublisherName(rs.getString("publisher_name")))
                .address(new Address(
                        rs.getString("state"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("home")
                        )
                )
                .phone(new Phone(rs.getString("phone")))
                .email(new Email(rs.getString("email")))
                .events(new Events(
                        rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                        rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                        )
                )
                .build();
    }
}
