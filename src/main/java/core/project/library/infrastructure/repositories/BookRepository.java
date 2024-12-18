package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Book;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.repositories.sql_mappers.RowToAuthor;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import core.project.library.infrastructure.repositories.sql_mappers.RowToPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    private final Optional<RowToBook> rowToBook;

    public BookRepository(JdbcTemplate jdbcTemplate, Optional<RowToBook> rowToBook,
                          Optional<RowToPublisher> rowToPublisher, Optional<RowToAuthor> rowToAuthor,
                          Optional<RowToOrder> rowToOrder) {
        if (rowToBook.isEmpty()) log.info("RowToBook is empty.");

        this.jdbcTemplate = jdbcTemplate;
        this.rowToBook = rowToBook;
    }

    public Optional<Book> getBookById(UUID bookId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where id=?", rowToBook.get(), bookId)
            );
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Book> findByName(String title) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where title=?", rowToBook.get(), title)
            );
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Book> getBooksByOrderId(UUID orderId) {
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
        return bookList;
    }

    public Page<Book> listOfBooks(Pageable pageable) {
        String pageNumber = String.valueOf(pageable.getPageNumber());
        String pageSize = String.valueOf(pageable.getPageSize());
        List<Book> list = jdbcTemplate.query(
                "Select * from Book Limit %s Offset %s".formatted(pageSize, pageNumber),
                rowToBook.orElseThrow()
        );

        return new PageImpl<>(list, pageable, pageable.getPageSize());
    }

    public Book saveBook(Book book) {
        Book bookForSave = Book.builder()
                .id(UUID.randomUUID())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .quantityOnHand(book.getQuantityOnHand())
                .category(book.getCategory())
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .publisher(book.getPublisher())
                .authors(book.getAuthors())
                .orders(new HashSet<>())
                .build();

        jdbcTemplate.update("""
        Insert into Book (id, title, description, isbn, price,
                  quantity_on_hand, category, created_date, last_modified_date)
                  values (?,?,?,?,?,?,?,?,?)
        """,
                bookForSave.getId().toString(), bookForSave.getTitle().title(), bookForSave.getDescription().description(),
                bookForSave.getIsbn().isbn(), bookForSave.getPrice(), bookForSave.getQuantityOnHand(), bookForSave.getCategory().toString(),
                bookForSave.getEvents().creation_date(), bookForSave.getEvents().last_update_date()
        );
        return bookForSave;
    }
}
