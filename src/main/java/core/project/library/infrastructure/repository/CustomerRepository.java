package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Result<Customer, EmptyResultDataAccessException> findById(UUID customerId) {
        try {
            String findById = "SELECT * FROM Customers WHERE id=?";

            return Result.success(
                    jdbcTemplate.queryForObject(findById, this::mapCustomer, customerId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Result.failure(e);
        }
    }

    public Result<List<Customer>, Exception> findByLastName(String lastName) {
        try {
            String findByLastName = "SELECT * FROM Customers WHERE last_name=?";

            List<Customer> customers = jdbcTemplate.query(findByLastName, this::mapCustomer, lastName);

            if (customers.isEmpty()) {
                return Result.failure(new NotFoundException("Customer not found"));
            } else {
                return Result.success(customers);
            }

        } catch (EmptyResultDataAccessException e) {
            return Result.failure(e);
        }
    }

    public Result<Customer, Exception> saveCustomer(Customer customer) {
        try {
            if (emailExists(customer.getEmail())) {
                return Result.failure(new IllegalArgumentException("Email already exists"));
            }

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

            return Result.success(customer);
        } catch (DataAccessException e) {
            return Result.failure(e);
        }
    }

    private Customer mapCustomer(ResultSet rs, int ignored) throws SQLException {
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

        return Customer.builder()
                .id(UUID.fromString(rs.getString("id")))
                .firstName(new FirstName(rs.getString("first_name")))
                .lastName(new LastName(rs.getString("last_name")))
                .password(new Password(rs.getString("password")))
                .email(new Email(rs.getString("email")))
                .address(address)
                .events(events)
                .build();
    }

    private boolean emailExists(Email email) {
        String findEmail = "SELECT COUNT(*) FROM Customers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(
                findEmail,
                Integer.class,
                email.email());

        return count != null && count > 0;
    }
}
