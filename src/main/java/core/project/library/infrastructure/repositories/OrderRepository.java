package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import core.project.library.infrastructure.repositories.sql_mappers.RowToCustomer;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Slf4j
@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    private final Optional<RowToOrder> rowToOrder;

    private final Optional<RowToCustomer> rowToCustomer;

    private final Optional<RowToBook> rowToBook;

    public OrderRepository(JdbcTemplate jdbcTemplate,
                           Optional<RowToOrder> rowToOrder,
                           Optional<RowToCustomer> rowToCustomer,
                           Optional<RowToBook> rowToBook) {
        if (rowToOrder.isEmpty()) log.info("RowToOrder is empty.");
        if (rowToCustomer.isEmpty()) log.info("RowToCustomer is empty.");
        if (rowToBook.isEmpty()) log.info("RowToBook is empty.");

        this.jdbcTemplate = jdbcTemplate;
        this.rowToOrder = rowToOrder;
        this.rowToCustomer = rowToCustomer;
        this.rowToBook = rowToBook;
    }

    public Optional<Order> getOrderById(String orderId) {
        Optional<Order> order = Optional.ofNullable(jdbcTemplate.queryForObject(
                "Select * from Order_Line where id=?", rowToOrder.orElseThrow(), orderId
        ));

        Optional<UUID> customerId = Optional.ofNullable(jdbcTemplate.queryForObject(
                "Select customer_id from Customer_Order where order_id=?",
                UUID.class, orderId
        ));

        Optional<Customer> customer = Optional.ofNullable(jdbcTemplate.queryForObject(
                "Select * from Customer where id=?", rowToCustomer.orElseThrow(), customerId.orElseThrow()
        ));

        List<UUID> books_uuids = jdbcTemplate.queryForList(
                "Select book_id from Book_Order where order_id=?", UUID.class, orderId
        );

        List<Book> bookList = new ArrayList<>();
        for (UUID bookId : books_uuids) {
            Optional<Book> optional = Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where id=?", rowToBook.orElseThrow(), bookId)
            );
            bookList.add(optional.orElseThrow());
        }

        return Optional.ofNullable(
                Order.builder()
                        .id(order.get().getId())
                        .countOfBooks(order.get().getCountOfBooks())
                        .totalPrice(order.get().getTotalPrice())
                        .events(order.get().getEvents())
                        .customer(customer.orElseThrow())
                        .books(new HashSet<>(bookList))
                        .build()
        );
    }
}
