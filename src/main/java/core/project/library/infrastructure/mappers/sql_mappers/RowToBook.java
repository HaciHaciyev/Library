package core.project.library.infrastructure.mappers.sql_mappers;

import core.project.library.domain.entities.Book;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.UUID;

@Component
public class RowToBook implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (!rs.next()) return null;

        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");

        return Book.builder()
                .id(UUID.fromString(rs.getString("id")))
                .title(new Title(rs.getString("title")))
                .description(new Description(rs.getString("description")))
                .isbn(new ISBN(rs.getString("isbn")))
                .price(new BigDecimal(rs.getString("price")))
                .quantityOnHand(rs.getInt("quantity_on_hand"))
                .events(new Events(
                        LocalDateTime.parse(rs.getString("created_date"), formatter),
                        LocalDateTime.parse(rs.getString("last_modified_date"), formatter)
                        )
                )
                .category(Category.valueOf(rs.getString("category")))
                .authors(new HashSet<>())
                .orders(new HashSet<>())
                .build();
    }
}
