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

    private static final String GET_BY_LAST_NAME =
            "Select * from Customers where last_name=?";

    private static final String GET_BY_ID =
            "Select * from Customers where id=?";

    private static final String FIND_EMAIL =
            "Select email from Customers where email=?";

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public void saveCustomer(Customer customer) {
        jdbcTemplate.update("""
                        Insert into Customers (id, first_name, last_name, email, password,
                                      state, city, street, home,
                                      creation_date, last_modified_date)
                                      values (?,?,?,?,?,?,?,?,?,?,?)
                        """,
                customer.getId().toString(), customer.getFirstName().firstName(), customer.getLastName().lastName(),
                customer.getEmail().email(), customer.getPassword().password(), customer.getAddress().state(),
                customer.getAddress().city(), customer.getAddress().street(), customer.getAddress().home(),
                customer.getEvents().creation_date(), customer.getEvents().last_update_date()
        );
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
                        rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                        rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                )).build();
    }
}
