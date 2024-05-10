package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Customer;
import core.project.library.infrastructure.repositories.sql_mappers.RowToCustomer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowToCustomer rowToCustomer;

    public CustomerRepository(JdbcTemplate jdbcTemplate, RowToCustomer rowToCustomer) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToCustomer = rowToCustomer;
    }

    public Optional<Customer> getCustomerById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Customer where id=?", rowToCustomer, customerId)
            );
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Customer> getCustomerByOrderId(UUID orderId) {
        Optional<UUID> customerId = Optional.ofNullable(jdbcTemplate.queryForObject(
                "Select customer_id from Customer_Order where order_id=?",
                UUID.class, orderId
        ));

        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "Select * from Customer where id=?", rowToCustomer, customerId.orElseThrow()
        ));
    }
}
