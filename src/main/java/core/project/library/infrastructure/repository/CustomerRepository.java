package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import org.springframework.dao.DataAccessException;
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
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean emailExists(Email email) {
        String findEmail = "SELECT COUNT(*) FROM Customers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(
                findEmail,
                Integer.class,
                email.email());
        return count != null && count > 0;
    }

    public Optional<Customer> findById(UUID customerId) {
        try {
            String findById = "SELECT * FROM Customers WHERE id=?";

            return Optional.of(
                    jdbcTemplate.queryForObject(findById, this::customerMapper, customerId.toString())
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Customer> findByLastName(String lastName) {
        try {
            String findByLastName = "SELECT * FROM Customers WHERE last_name=?";

            return jdbcTemplate.query(
                    findByLastName, this::customerMapper, lastName
            );

        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public Optional<Customer> saveCustomer(Customer customer) {
        try {
            String saveCustomer = """
                    INSERT INTO Customers (id, first_name, last_name, email, password,
                                  state, city, street, home,
                                  creation_date, last_modified_date)
                                  VALUES (?,?,?,?,?,?,?,?,?,?,?)
                    """;

            jdbcTemplate.update(saveCustomer,
                    customer.getId().toString(), customer.getFirstName().firstName(), customer.getLastName().lastName(),
                    customer.getEmail().email(), customer.getPassword().password(), customer.getAddress().state(),
                    customer.getAddress().city(), customer.getAddress().street(), customer.getAddress().home(),
                    customer.getEvents().creation_date(), customer.getEvents().last_update_date()
            );

            return Optional.of(customer);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Customer customerMapper(ResultSet rs, int rowNum) throws SQLException {
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

        return Customer.create(
                UUID.fromString(rs.getString("id")),
                new FirstName(rs.getString("first_name")),
                new LastName(rs.getString("last_name")),
                new Password(rs.getString("password")),
                new Email(rs.getString("email")),
                address,
                events
        );
    }
}
