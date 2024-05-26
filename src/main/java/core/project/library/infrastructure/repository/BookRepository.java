package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Book> findById(UUID bookId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlForGetBook, new RowToBook(), bookId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Book> findByTitle(String title) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlForGetBookByTitle, new RowToBook(), title)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Book>> listOfBooks(Integer pageNumber,
                                  Integer pageSize) {
        try {
            int offSet;
            if (pageNumber != 0) {
                offSet = (pageNumber - 1) * pageSize;
            } else {
                offSet = 0;
            }

            return Optional.of(jdbcTemplate.query(sqlForListOfBooks,
                    new RowToBook(), pageSize, offSet));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Book>> listByCategory(Integer pageNumber,
                                     Integer pageSize, String category) {
        try {
            int offSet;
            if (pageNumber != 0) {
                offSet = (pageNumber - 1) * pageSize;
            } else {
                offSet = 0;
            }

            return Optional.of(jdbcTemplate.query(sqlForBooksByCategory, new RowToBook(),
                    category, pageSize, offSet));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static final String byId = "WHERE b.id = ?";

    private static final String sqlForGetBook = String.format("""
                SELECT
                    b.id AS book_id,
                    b.title AS book_title,
                    b.description AS book_description,
                    b.isbn AS book_isbn,
                    b.price AS book_price,
                    b.quantity_on_hand AS book_quantity,
                    b.category AS book_category,
                    b.created_date AS book_created_date,
                    b.last_modified_date AS book_last_modified_date,
                
                    p.id AS publisher_id,
                    p.publisher_name AS publisher_name,
                    p.state AS publisher_state,
                    p.city AS publisher_city,
                    p.street AS publisher_street,
                    p.home AS publisher_home,
                    p.phone AS publisher_phone,
                    p.email AS publisher_email,
                    p.creation_date AS publisher_creation_date,
                    p.last_modified_date AS publisher_last_modified_date,
                
                    a.id AS author_id,
                    a.first_name AS author_first_name,
                    a.last_name AS author_last_name,
                    a.email AS author_email,
                    a.state AS author_state,
                    a.city AS author_city,
                    a.street AS author_street,
                    a.home AS author_home,
                    a.created_date AS author_created_date,
                    a.last_modified_date AS author_last_modified_date
                FROM Book b
                INNER JOIN Publisher p ON b.publisher_id = p.id
                INNER JOIN Book_Author ba ON b.id = ba.book_id
                INNER JOIN Author a ON ba.author_id = a.id
                %s
                """, byId);

    private static final String sqlForGetBookByTitle = sqlForGetBook.replace(
            byId, "WHERE b.title = ?"
    );

    private static final String sqlForListOfBooks = sqlForGetBook.replace(
            byId, "LIMIT ? OFFSET ?"
    );

    private static final String sqlForBooksByCategory = sqlForGetBook.replace(
            byId, "WHERE b.category = ? LIMIT ? OFFSET ?"
    );

    private static final class RowToBook implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                Publisher publisher = Publisher.builder()
                        .id(UUID.fromString(rs.getString("publisher_id")))
                        .publisherName(new PublisherName(rs.getString("publisher_name")))
                        .address(new Address(
                                rs.getString("publisher_state"),
                                rs.getString("publisher_city"),
                                rs.getString("publisher_street"),
                                rs.getString("publisher_home")
                        ))
                        .phone(new Phone(rs.getString("publisher_phone")))
                        .email(new Email(rs.getString("publisher_email")))
                        .events(new Events(
                                        rs.getObject("publisher_creation_date",
                                                Timestamp.class).toLocalDateTime(),
                                        rs.getObject("publisher_last_modified_date",
                                                Timestamp.class).toLocalDateTime()
                                )
                        )
                        .build();

                Author author = Author.builder()
                        .id(UUID.fromString(rs.getString("author_id")))
                        .firstName(new FirstName(rs.getString("author_first_name")))
                        .lastName(new LastName(rs.getString("author_last_name")))
                        .email(new Email(rs.getString("author_email")))
                        .address(new Address(
                                rs.getString("author_state"),
                                rs.getString("author_city"),
                                rs.getString("author_street"),
                                rs.getString("author_home")
                        ))
                        .events(new Events(
                                rs.getObject("author_created_date", Timestamp.class).toLocalDateTime(),
                                rs.getObject("author_last_modified_date", Timestamp.class).toLocalDateTime()
                        ))
                        .build();

                return Book.builder()
                        .id(UUID.fromString(rs.getString("book_id")))
                        .title(new Title(rs.getString("book_title")))
                        .description(new Description(rs.getString("book_description")))
                        .isbn(new ISBN(rs.getString("book_isbn")))
                        .price(new BigDecimal(rs.getString("book_price")))
                        .quantityOnHand(rs.getInt("book_quantity"))
                        .category(Category.valueOf(rs.getString("book_category")))
                        .events(new Events(
                                        rs.getObject("book_created_date", Timestamp.class).toLocalDateTime(),
                                        rs.getObject("book_last_modified_date", Timestamp.class).toLocalDateTime()
                                )
                        )
                        .publisher(publisher)
                        .authors(new HashSet<>(Collections.singleton(author)))
                        .build();
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
    }
}
