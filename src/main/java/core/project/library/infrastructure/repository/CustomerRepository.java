package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
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
public class CustomerRepository {

    private static final String GET_BY_LAST_NAME = "Select * from Customer where last_name = ?";
    private static final String GET_BY_ID = "Select * from Customer where id = ?";

    private final JdbcTemplate jdbcTemplate;


    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(GET_BY_ID, this::customerMapper, customerId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Customer>> findByLastName(String lastName) {
        try {
            return Optional.of(jdbcTemplate.query(GET_BY_LAST_NAME, this::customerMapper, lastName));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    final Customer customerMapper(ResultSet rs, int ignored) throws SQLException {
        return Customer.builder()
                .id(UUID.fromString(rs.getString("id")))
                .firstName(new FirstName(rs.getString("first_name")))
                .lastName(new LastName(rs.getString("last_name")))
                .password(new Password(rs.getString("password")))
                .email(new Email(rs.getString("email")))
                .address(new Address(
                        rs.getString("state"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("home")))
                .events(new Events(
                        rs.getObject("created_date", Timestamp.class).toLocalDateTime(),
                        rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                )).build();
    }
}
