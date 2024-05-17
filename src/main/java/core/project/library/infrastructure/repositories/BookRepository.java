package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Order;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowToBook rowToBook;

    public BookRepository(JdbcTemplate jdbcTemplate, RowToBook rowToBook) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToBook = rowToBook;
    }

    public Optional<Book> getBookById(UUID bookId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where id=?", rowToBook, bookId.toString())
            );
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Book> findByName(String title) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where title=?", rowToBook, title)
            );
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Book> getBooksByOrderId(UUID orderId) {
        List<String> bookUUIDs = jdbcTemplate.queryForList(
                "Select book_id from Book_Order where order_id=?", String.class, orderId.toString()
        );

        List<Book> bookList = new ArrayList<>();
        for (String bookId : bookUUIDs) {
            Optional<Book> optional = Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where id=?", rowToBook, bookId)
            );
            bookList.add(optional.orElseThrow());
        }

        return bookList;
    }

    public Page<Book> listOfBooks(Pageable pageable) {
        String pageNumber =
                String.valueOf(pageable.getPageNumber());
        String pageSize =
                String.valueOf(pageable.getPageSize());
        List<Book> list =
                jdbcTemplate.query(
                "Select * from Book Limit %s Offset %s"
                .formatted(pageSize, pageNumber),
                rowToBook);

        return new PageImpl<>(list, pageable, pageable.getPageSize());
    }

    public Optional<Book> saveBook(Book bookForSave) {
        jdbcTemplate.update("""
        Insert into Book (id, title, description, isbn, price,
                  quantity_on_hand, category, created_date, last_modified_date)
                  values (?,?,?,?,?,?,?,?,?)
        """,
                bookForSave.getId().toString(), bookForSave.getTitle().title(), bookForSave.getDescription().description(),
                bookForSave.getIsbn().isbn(), bookForSave.getPrice(), bookForSave.getQuantityOnHand(), bookForSave.getCategory().toString(),
                bookForSave.getEvents().creation_date(), bookForSave.getEvents().last_update_date()
        );
        return Optional.of(bookForSave);
    }

    public void updateBook(UUID bookId, Book bookForUpdate) {
        jdbcTemplate.update("""
               Update Book
               Set
                    title = ?,
                    description = ?,
                    isbn = ?,
                    price = ?,
                    quantity_on_hand = ?,
                    category = ?,
                    last_modified_date = ?
               Where id = ?
        """,
                bookForUpdate.getTitle().title(), bookForUpdate.getDescription().description(),
                bookForUpdate.getIsbn().isbn(), bookForUpdate.getPrice(), bookForUpdate.getQuantityOnHand(),
                bookForUpdate.getCategory().toString(), bookForUpdate.getEvents().last_update_date(),
                bookId.toString()
        );
    }

    public void patchBook(UUID bookId, Map<String, String> values) {
        if (values.containsKey("title")) {
            jdbcTemplate.update("""
                    Update Book
                    Set
                        title = ?
                    Where id = ?
            """,
                    values.get("title"), bookId.toString()
            );
        }
        if (values.containsKey("description")) {
            jdbcTemplate.update("""
                    Update Book
                    Set
                        description = ?
                    Where id = ?
            """,
                    values.get("description"), bookId.toString()
            );
        }
        if (values.containsKey("price")) {
            jdbcTemplate.update("""
                    Update Book
                    Set
                        price = ?
                    Where id = ?
            """,
                    new BigDecimal(values.get("price")), bookId.toString()
            );
        }
        if (values.containsKey("quantity_on_hand")) {
            jdbcTemplate.update("""
                    Update Book
                    Set
                        quantity_on_hand = ?
                    Where id = ?
            """,
                    Integer.valueOf(values.get("quantity_on_hand")), bookId.toString()
            );
        }
        if (values.containsKey("category")) {
            jdbcTemplate.update("""
                    Update Book
                    Set
                        category = ?
                    Where id = ?
            """,
                    values.get("category"), bookId.toString()
            );
        }
    }

    public void saveBookPublisher(Book savedBook, Publisher savedPublisher) {
        jdbcTemplate.update("""
        Insert into Book_Publisher (id, book_id, publisher_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                savedBook.getId().toString(),
                savedPublisher.getId().toString()
        );
    }

    public void saveBookAuthor(Book savedBook, Author savedAuthor) {
        jdbcTemplate.update("""
        Insert into Book_Author (id, book_id, author_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                savedBook.getId().toString(),
                savedAuthor.getId().toString()
        );
    }

    public void saveBookOrder(Book existingBook, Order existingOrder) {
       jdbcTemplate.update("""
       Insert into Book_Order (id, book_id, order_id)
                   values (?,?,?)
       """,
                UUID.randomUUID().toString(),
                existingBook.getId().toString(),
                existingOrder.getId().toString()
       );
    }
}
