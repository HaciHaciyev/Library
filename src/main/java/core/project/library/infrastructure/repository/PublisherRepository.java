package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
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

    private static final String GET_BY_ID = "Select * from Publisher where id = ?";
    private static final String GET_BY_NAME = "Select * from Publisher where publisher_name = ?";

    private final JdbcTemplate template;

    public PublisherRepository(JdbcTemplate template) {
        this.template = template;
    }

    public Optional<Publisher> findById(UUID publisherId) {
        try {
            return Optional.ofNullable(
                    template.queryForObject(GET_BY_ID, this::publisherMapper, publisherId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Publisher>> findByName(String name) {
        try {
            return Optional.of(
                    template.query(GET_BY_NAME, this::publisherMapper, name)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Publisher publisherMapper(ResultSet rs, int ignored) throws SQLException {
        return Publisher.builder()
                .id(UUID.fromString(rs.getString("id")))
                .publisherName(new PublisherName(rs.getString("publisher_name")))
                .address(new Address(
                        rs.getString("state"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("home")))
                .email(new Email(rs.getString("email")))
                .events(new Events(
                        rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                        rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                )).build();
    }
}
