package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Order;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.sql_mappers.RowToAuthor;
import core.project.library.infrastructure.sql_mappers.RowToBook;
import core.project.library.infrastructure.sql_mappers.RowToOrder;
import core.project.library.infrastructure.sql_mappers.RowToPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class BookRepository {

    private final Optional<JdbcTemplate> jdbcTemplate;

    private final Optional<RowToBook> rowToBook;

    private final Optional<RowToPublisher> rowToPublisher;

    private final Optional<RowToAuthor> rowToAuthor;

    private final Optional<RowToOrder> rowToOrder;

    public BookRepository(Optional<JdbcTemplate> jdbcTemplate,
                          Optional<RowToBook> rowToBook,
                          Optional<RowToPublisher> rowToPublisher,
                          Optional<RowToAuthor> rowToAuthor,
                          Optional<RowToOrder> rowToOrder) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToBook = rowToBook;
        this.rowToPublisher = rowToPublisher;
        this.rowToAuthor = rowToAuthor;
        this.rowToOrder = rowToOrder;
    }

    public Optional<Book> getBookById(String bookId) {
        if (jdbcTemplate.isEmpty()) {
            throw new RuntimeException("JdbcTemplate dependency doesn`t exists in BookRepository class.");
        }

        return Optional.ofNullable(jdbcTemplate.get()
                .queryForObject("Select * from Book where id=?", rowToBook.orElseThrow(), bookId)
        );
    }

    public Optional<Publisher> getBookPublisher(String bookId) {
        if (jdbcTemplate.isEmpty()) {
            throw new RuntimeException("JdbcTemplate dependency doesn`t exists in BookRepository class.");
        }

        Optional<UUID> publisherId = Optional.ofNullable(jdbcTemplate.get()
                .queryForObject("Select publisher_id from Book_Publisher where book_id=?",
                        UUID.class, bookId));

        return Optional.ofNullable(jdbcTemplate.get()
                .queryForObject("Select * from Publisher where id=?",
                rowToPublisher.orElseThrow(), publisherId.orElseThrow(NotFoundException::new))
        );
    }

    public List<Optional<Author>> getBookAuthors(String bookId) {
        if (jdbcTemplate.isEmpty()) {
            throw new RuntimeException("JdbcTemplate dependency doesn`t exists in BookRepository class.");
        }

        List<UUID> uuids = jdbcTemplate.get()
                .queryForList("Select author_id from Book_Author where book_id=?",
                UUID.class, bookId);

        List<Optional<Author>> authors = new ArrayList<>();
        for (UUID authorId : uuids) {
            Optional<Author> optional = Optional.ofNullable(jdbcTemplate.get()
                    .queryForObject("Select * from Author where id=?",
                            rowToAuthor.orElseThrow(), authorId)
            );
            authors.add(optional);
        }
        return authors;
    }

    public List<Optional<Order>> getBookOrders(String bookId) {
        if (jdbcTemplate.isEmpty()) {
            throw new RuntimeException("JdbcTemplate dependency doesn`t exists in BookRepository class.");
        }

        List<UUID> uuids = jdbcTemplate.get().queryForList("Select order_id from Book_Order where book_id=?",
                UUID.class, bookId);

        List<Optional<Order>> orders = new ArrayList<>();
        for (UUID orderId : uuids) {
            Optional<Order> optional = Optional.ofNullable(jdbcTemplate.get()
                    .queryForObject("Select * from Order_Line where id=?",
                            rowToOrder.orElseThrow(), orderId)
            );
            orders.add(optional);
        }
        return orders;
    }
}
