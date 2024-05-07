package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Book;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}
