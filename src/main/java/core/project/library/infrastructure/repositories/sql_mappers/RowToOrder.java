package core.project.library.infrastructure.repositories.sql_mappers;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.data_transfer.OrderDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Component
public class RowToOrder implements RowMapper<OrderDTO> {
    @Override
    public OrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return new OrderDTO(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("customer_id")),
                    Integer.valueOf(rs.getString("count_of_book")),
                    new TotalPrice(new BigDecimal(rs.getString("total_price"))),
                    new Events(rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                               rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()));

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
