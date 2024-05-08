package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import core.project.library.infrastructure.repositories.sql_mappers.RowToCustomer;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import lombok.extern.slf4j.Slf4j;
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

    public OrderRepository(JdbcTemplate jdbcTemplate,
                           RowToOrder rowToOrder) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToOrder = rowToOrder;
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "Select * from Order_Line where id=?", rowToOrder, orderId
        ));
    }

    public List<Optional<Order>> getOrderByBookId(UUID bookId) {
        List<UUID> uuids = jdbcTemplate.queryForList("Select order_id from Book_Order where book_id=?",
                UUID.class, bookId);

        List<Optional<Order>> orders = new ArrayList<>();
        uuids.forEach(uuid -> orders.add(Optional.ofNullable(jdbcTemplate
                .queryForObject("Select * from Order_Line where id=?", rowToOrder, uuid)
        )));
        return orders;
    }
}
