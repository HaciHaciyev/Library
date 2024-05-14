package core.project.library.infrastructure.repositories.sql_mappers;

import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Component
public class RowToOrder implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return Order.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .countOfBooks(Integer.valueOf(rs.getString("count_of_book")))
                    .totalPrice(new TotalPrice(new BigDecimal(rs.getString("total_price"))))
                    .events(new Events(
                                    rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                                    rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                            )
                    )
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
