package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Order;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.sql_mappers.RowToAuthor;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import core.project.library.infrastructure.repositories.sql_mappers.RowToPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    private final Optional<RowToBook> rowToBook;

    private final Optional<RowToPublisher> rowToPublisher;

    private final Optional<RowToAuthor> rowToAuthor;

    private final Optional<RowToOrder> rowToOrder;

    public BookRepository(JdbcTemplate jdbcTemplate, Optional<RowToBook> rowToBook,
                          Optional<RowToPublisher> rowToPublisher, Optional<RowToAuthor> rowToAuthor,
                          Optional<RowToOrder> rowToOrder) {
        if (rowToBook.isEmpty()) log.info("RowToBook is empty.");
        if (rowToPublisher.isEmpty()) log.info("RowToPublisher is empty.");
        if (rowToAuthor.isEmpty()) log.info("RowToAuthor is empty.");
        if (rowToOrder.isEmpty()) log.info("RowToOrder is empty.");

        this.jdbcTemplate = jdbcTemplate;
        this.rowToBook = rowToBook;
        this.rowToPublisher = rowToPublisher;
        this.rowToAuthor = rowToAuthor;
        this.rowToOrder = rowToOrder;
    }

    public Optional<Book> getBookById(String bookId) {
        return Optional.ofNullable(jdbcTemplate
                .queryForObject("Select * from Book where id=?", rowToBook.orElseThrow(), bookId)
        );
    }

    public Optional<Publisher> getBookPublisher(String bookId) {
        Optional<UUID> publisherId = Optional.ofNullable(jdbcTemplate
                .queryForObject("Select publisher_id from Book_Publisher where book_id=?",
                        UUID.class, bookId));

        return Optional.ofNullable(jdbcTemplate
                .queryForObject("Select * from Publisher where id=?",
                rowToPublisher.orElseThrow(), publisherId.orElseThrow(NotFoundException::new))
        );
    }

    public List<Optional<Author>> getBookAuthors(String bookId) {
        List<UUID> uuids = jdbcTemplate.queryForList("Select author_id from Book_Author where book_id=?",
                UUID.class, bookId);

        List<Optional<Author>> authors = new ArrayList<>();
        for (UUID authorId : uuids) {
            Optional<Author> optional = Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Author where id=?", rowToAuthor.orElseThrow(), authorId)
            );
            authors.add(optional);
        }
        return authors;
    }

    public List<Optional<Order>> getBookOrders(String bookId) {
        List<UUID> uuids = jdbcTemplate.queryForList("Select order_id from Book_Order where book_id=?",
                UUID.class, bookId);

        List<Optional<Order>> orders = new ArrayList<>();
        for (UUID orderId : uuids) {
            Optional<Order> optional = Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Order_Line where id=?", rowToOrder.orElseThrow(), orderId)
            );
            orders.add(optional);
        }
        return orders;
    }
}
