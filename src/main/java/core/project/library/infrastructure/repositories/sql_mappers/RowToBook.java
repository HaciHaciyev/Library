package core.project.library.infrastructure.repositories.sql_mappers;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import core.project.library.infrastructure.data_transfer.BookDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Component
public class RowToBook implements RowMapper<BookDTO> {
    @Override
    public BookDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return BookDTO.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .publisherId(UUID.fromString(rs.getString("publisher_id")))
                    .title(new Title(rs.getString("title")))
                    .description(new Description(rs.getString("description")))
                    .isbn(new ISBN(rs.getString("isbn")))
                    .price(new BigDecimal(rs.getString("price")))
                    .quantityOnHand(rs.getInt("quantity_on_hand"))
                    .events(new Events(
                                    rs.getObject("created_date", Timestamp.class).toLocalDateTime(),
                                    rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                            )
                    )
                    .category(Category.valueOf(rs.getString("category")))
                    .build();
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
