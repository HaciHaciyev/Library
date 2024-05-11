package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.repositories.sql_mappers.RowToBook;
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

    private final RowToBook rowToBook;

    public BookRepository(JdbcTemplate jdbcTemplate, RowToBook rowToBook) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToBook = rowToBook;
    }

    public Optional<Book> getBookById(UUID bookId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where id=?", rowToBook, bookId)
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
    //TODO refactor this by StreamAPI
    public List<Book> getBooksByOrderId(UUID orderId) {
        List<UUID> books_uuids = jdbcTemplate.queryForList(
                "Select book_id from Book_Order where order_id=?", UUID.class, orderId
        );

        List<Book> bookList = new ArrayList<>();
        for (UUID bookId : books_uuids) {
            Optional<Book> optional = Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Book where id=?", rowToBook, bookId)
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
                rowToBook);

        return new PageImpl<>(list, pageable, pageable.getPageSize());
    }

    public Optional<Book> saveBook(Book book) {
        Book bookForSave = Book.builder()
                .id(UUID.randomUUID())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .quantityOnHand(book.getQuantityOnHand())
                .events(new Events())
                .category(book.getCategory())
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
        return Optional.of(bookForSave);
    }

    public void saveBook_Publisher(Book savedBook, Publisher savedPublisher) {
        jdbcTemplate.update("""
        Insert into Book_Publisher (id, book_id, publisher_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                savedBook.getId().toString(),
                savedPublisher.getId().toString()
        );
    }

    public void saveBook_Author(Book savedBook, Author savedAuthor) {
        jdbcTemplate.update("""
        Insert into Book_Author (id, book_id, author_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                savedBook.getId().toString(),
                savedAuthor.getId().toString()
        );
    }
}
