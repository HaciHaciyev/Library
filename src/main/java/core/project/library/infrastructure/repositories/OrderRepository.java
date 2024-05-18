package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

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

    public UUID getCustomerId(UUID orderId) {
        return UUID.fromString(Objects.requireNonNull(jdbcTemplate.queryForObject(
                "Select customer_id from Order_Line where id=?", String.class, orderId.toString()
        )));
    }

    public List<Order> getOrdersByCustomerId(UUID customerId) {
        List<Order> orders = new ArrayList<>();
        List<Map<String, Object>> query = jdbcTemplate.queryForList(
                "Select * from Order_Line where customer_id=?", customerId.toString()
        );
        query.forEach(rs -> orders.add(Order.builder()
                .id(UUID.fromString(rs.get("id").toString()))
                .countOfBooks(Integer.valueOf(rs.get("count_of_book").toString()))
                .totalPrice(new TotalPrice(new BigDecimal(rs.get("total_price").toString())))
                .events(new Events(
                        Timestamp.valueOf(rs.get("creation_date").toString()).toLocalDateTime(),
                        Timestamp.valueOf(rs.get("last_modified_date").toString()).toLocalDateTime()
                        )
                )
                .build()
        ));

        return orders;
    }

    public List<Optional<Order>> getOrdersByBookId(UUID bookId) {
        List<String> uuids = jdbcTemplate.queryForList("Select order_id from Book_Order where book_id=?",
                String.class, bookId.toString());

        List<Optional<Order>> orders = new ArrayList<>();
        uuids.forEach(uuid -> orders.add(Optional.ofNullable(
                jdbcTemplate.queryForObject(SELECT_BY_ID, rowToOrder, uuid)
        )));
        return orders;
    }

    public Optional<Order> saveOrder(Order order) {
        jdbcTemplate.update("""
        Insert into Order_Line (id, customer_id, count_of_book, total_price,
                        creation_date, last_modified_date)
                        values (?,?,?,?,?,?)
        """,
                order.getId().toString(), order.getCustomer().getId().toString(),
                order.getCountOfBooks(), order.getTotalPrice().totalPrice(),
                order.getEvents().creation_date(), order.getEvents().last_update_date()
        );
        return Optional.of(order);
    }
}
