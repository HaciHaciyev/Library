package core.project.library.infrastructure.sql_mappers;

import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.UUID;

@Component
public class RowToAuthor implements RowMapper<Author> {
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
                        )
                )
                .events(new Events(
                                rs.getObject("created_date", Timestamp.class).toLocalDateTime(),
                                rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                        )
                )
                .books(new HashSet<>())
                .build();
    }
}
