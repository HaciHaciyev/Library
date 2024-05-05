package core.project.library.infrastructure.repositories.sql_mappers;

import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.UUID;

@Component
public class RowToCustomer implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Customer.builder()
                .id(UUID.fromString(rs.getString("id")))
                .firstName(new FirstName("first_name"))
                .lastName(new LastName("last_name"))
                .email(new Email(rs.getString("email")))
                .password(new Password(rs.getString("password")))
                .address(new Address(
                                rs.getString("state"),
                                rs.getString("city"),
                                rs.getString("street"),
                                rs.getString("home")
                        )
                )
                .events(new Events(
                                rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                                rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                        )
                )
                .orders(new HashSet<>())
                .build();
    }
}
