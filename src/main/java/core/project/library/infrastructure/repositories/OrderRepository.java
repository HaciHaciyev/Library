package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowToOrder rowToOrder;

    private static final String SELECT_BY_ID = "Select * from Order_Line where id=?";

    public OrderRepository(JdbcTemplate jdbcTemplate,
                           RowToOrder rowToOrder) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToOrder = rowToOrder;
    }

    public Optional<Order> getOrderById(UUID orderId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_ID,
                    rowToOrder, orderId.toString()
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Optional<Order>> getOrderByBookId(UUID bookId) {
        List<String> uuids = jdbcTemplate.queryForList("Select order_id from Book_Order where book_id=?",
                String.class, bookId.toString());

        List<Optional<Order>> orders = new ArrayList<>();
        uuids.forEach(uuid -> orders.add(Optional.ofNullable(
                jdbcTemplate.queryForObject(SELECT_BY_ID, rowToOrder, uuid)
        )));
        return orders;
    }

    public List<Optional<Order>> getOrdersByCustomerId(UUID customerId) {
        List<String> uuids = jdbcTemplate.queryForList("Select order_id from Customer_Order where customer_id=?",
                String.class, customerId.toString());

        List<Optional<Order>> orders = new ArrayList<>();
        uuids.forEach(uuid -> orders.add(Optional.ofNullable(
                jdbcTemplate.queryForObject(SELECT_BY_ID, rowToOrder, uuid)
        )));
        return orders;
    }

    public Optional<Order> saveOrder(Order order) {
        jdbcTemplate.update("""
        Insert into Order_Line (id, count_of_book, total_price,
                        creation_date, last_modified_date)
                        values (?,?,?,?,?)
        """,
                order.getId().toString(), order.getCountOfBooks(), order.getTotalPrice().totalPrice(),
                order.getEvents().creation_date(), order.getEvents().last_update_date()
        );
        return Optional.of(order);
    }
}
