package core.project.library.infrastructure.repositories.sql_mappers;

import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Component
public class RowToPublisher implements RowMapper<Publisher> {
    @Override
    public Publisher mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return Publisher.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .publisherName(new PublisherName(rs.getString("publisher_name")))
                    .address(new Address(
                            rs.getString("state"),
                            rs.getString("city"),
                            rs.getString("street"),
                            rs.getString("home")
                    ))
                    .phone(new Phone(rs.getString("phone")))
                    .email(new Email(rs.getString("email")))
                    .events(new Events(
                                    rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                                    rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                            )
                    )
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
