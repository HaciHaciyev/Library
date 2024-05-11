package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.repositories.sql_mappers.RowToCustomer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

    public void saveCustomer_Order(Customer existingCustomer, Order existingOrder) {
        jdbcTemplate.update("""
       Insert into Customer_Order (id, customer_id, order_id)
                   values (?,?,?)
       """,
                UUID.randomUUID().toString(),
                existingCustomer.getId().toString(),
                existingOrder.getId().toString());
    }

    public Optional<Customer> saveCustomer(Customer customer) {
        Customer customerForSave = Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .password(customer.getPassword())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .events(new Events())
                .orders(new HashSet<>())
                .build();

        jdbcTemplate.update("""
                        Insert into Customer (id, first_name, last_name, email, password,
                         state, city,street, home, creation_date, last_modified_date)
                         values (?,?,?,?,?,?,?,?,?,?,?)
                        """,
                customerForSave.getId().toString(), customerForSave.getFirstName().firstName(),
                customerForSave.getLastName().lastName(), customerForSave.getEmail().email(),
                customerForSave.getPassword().password(), customerForSave.getAddress().state(),
                customerForSave.getAddress().city(), customerForSave.getAddress().street(),
                customerForSave.getAddress().home(), customerForSave.getEvents().creation_date(),
                customerForSave.getEvents().last_update_date());

        return Optional.of(customerForSave);
    }
}
