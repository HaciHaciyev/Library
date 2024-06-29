package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer count() {
        return jdbcTemplate.queryForObject("Select COUNT(id) from Books", Integer.class);
    }

    public boolean isbnExists(ISBN verifiableIsbn) {
        try {
            String findISBN = "Select COUNT(*) from Books where isbn = ?";
            Integer count = jdbcTemplate.queryForObject(
                    findISBN,
                    Integer.class,
                    verifiableIsbn.isbn()
            );
            return count != null && count >  0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public Optional<Book> findById(UUID bookId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            connection -> connection.prepareStatement(
                                String.format(SQL_FOR_GET_BOOK_BY_ID, bookId.toString()),
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY
                            ),
                            new RowToBook()
                    )
            );
        } catch (EmptyResultDataAccessException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Optional<Book> findByISBN(ISBN isbn) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            connection -> connection.prepareStatement(
                                    String.format(SQL_FOR_GET_BOOK_BY_ISBN, isbn.isbn()),
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY
                            ),
                            new RowToBook()
                    )
            );
        } catch (EmptyResultDataAccessException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Result<List<Book>, NotFoundException> listOfBooks(Integer pageNumber, Integer pageSize) {
        try {
            final int limit = buildLimit(pageSize);
            final int offSet = buildOffSet(limit, pageNumber);

            String sqlForGetListOfBooks = String.format(SQL_FOR_GET_LIST_OF_BOOKS, limit, offSet);
            log.info(sqlForGetListOfBooks);

            List<Book> listOfBooks = jdbcTemplate.query(
                    connection -> connection.prepareStatement(
                            sqlForGetListOfBooks,
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY
                    ),
                    new ResultSetToListOfBooks()
            );

            if (listOfBooks == null || listOfBooks.isEmpty()) {
                return Result.failure(new NotFoundException("Books was not found"));
            } else {
                return Result.success(listOfBooks);
            }
        } catch (EmptyResultDataAccessException | NoSuchElementException e) {
            return Result.failure(new NotFoundException("Books was not found"));
        }
    }

    public void completelySaveBook(Book book) {
        jdbcTemplate.update("""
                        Insert into Books (id, publisher_id, title, description, isbn, price,
                                  quantity_on_hand, category, creation_date, last_modified_date)
                                  values (?,?,?,?,?,?,?,?,?,?)
                        """,
                book.getId().toString(),
                book.getPublisher().getId().toString(),
                book.getTitle().title(),
                book.getDescription().description(),
                book.getIsbn().isbn(),
                book.getPrice(),
                book.getQuantityOnHand(),
                book.getCategory().toString(),
                book.getEvents().creation_date(),
                book.getEvents().last_update_date()
        );

        for (Author author : book.getAuthors()) {
            jdbcTemplate.update("""
                        Insert into Book_Author (book_id, author_id)
                                    values (?,?)
                        """,
                    book.getId().toString(),
                    author.getId().toString()
            );
        }
    }

    public void patchBook(Book foundBook) {
        jdbcTemplate.update("""
            Update Books Set
                 description = ?,
                 price = ?,
                 quantity_on_hand = ?
            Where id = ?
            """,
                foundBook.getDescription().description(),
                foundBook.getPrice(),
                foundBook.getQuantityOnHand(),
                foundBook.getId().toString()
        );
    }

    public static int buildLimit(Integer pageSize) {
        int limit;
        if (pageSize > 0 && pageSize <= 25) {
            limit = pageSize;
        } else {
            limit = 10;
        }
        return limit;
    }

    public static int buildOffSet(Integer limit, Integer pageNumber) {
        int offSet;
        if (limit > 0 && pageNumber > 0) {
            offSet = (pageNumber - 1) * limit;
        } else {
            offSet = 0;
        }
        return offSet;
    }

    private static final String SQL_FOR_GET_BOOK_BY_ID = """
                SELECT
                    b.id AS book_id,
                    b.title AS book_title,
                    b.description AS book_description,
                    b.isbn AS book_isbn,
                    b.price AS book_price,
                    b.quantity_on_hand AS book_quantity,
                    b.category AS book_category,
                    b.creation_date AS book_creation_date,
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
                    a.creation_date AS author_creation_date,
                    a.last_modified_date AS author_last_modified_date
                FROM Books b
                INNER JOIN Publishers p ON b.publisher_id = p.id
                INNER JOIN Book_Author ba ON b.id = ba.book_id
                INNER JOIN Authors a ON ba.author_id = a.id
                WHERE b.id = '%s'
                """;

    private static final String SQL_FOR_GET_BOOK_BY_ISBN = SQL_FOR_GET_BOOK_BY_ID.replace(
            "WHERE b.id = '%s'", "WHERE b.isbn = '%s'"
    );

    private static final String SQL_FOR_GET_LIST_OF_BOOKS = SQL_FOR_GET_BOOK_BY_ID.replace(
            "WHERE b.id = '%s'", "LIMIT %s OFFSET %s"
    );

    private static final class RowToBook implements ResultSetExtractor<Book> {
        @Override
        public Book extractData(ResultSet rs) throws SQLException {
            try {
                rs.first();

                Publisher publisher = Publisher.builder()
                        .id(UUID.fromString(rs.getString("publisher_id")))
                        .publisherName(new PublisherName(rs.getString("publisher_name")))
                        .address(new Address(
                                rs.getString("publisher_state"),
                                rs.getString("publisher_city"),
                                rs.getString("publisher_street"),
                                rs.getString("publisher_home")
                                )
                        )
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

                Set<Author> authors = new LinkedHashSet<>();
                do {
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
                                    )
                            )
                            .events(new Events(
                                    rs.getObject("author_creation_date", Timestamp.class).toLocalDateTime(),
                                    rs.getObject("author_last_modified_date", Timestamp.class).toLocalDateTime()
                                    )
                            )
                            .build();
                    authors.add(author);
                } while (rs.next());
                rs.previous();

                return Book.builder()
                        .id(UUID.fromString(rs.getString("book_id")))
                        .title(new Title(rs.getString("book_title")))
                        .description(new Description(rs.getString("book_description")))
                        .isbn(new ISBN(rs.getString("book_isbn")))
                        .price(new Price(rs.getDouble("book_price")))
                        .quantityOnHand(new QuantityOnHand(rs.getInt("book_quantity")))
                        .category(Category.valueOf(rs.getString("book_category")))
                        .events(new Events(
                                        rs.getObject("book_creation_date", Timestamp.class).toLocalDateTime(),
                                        rs.getObject("book_last_modified_date", Timestamp.class).toLocalDateTime()
                                )
                        )
                        .publisher(publisher)
                        .authors(authors)
                        .build();
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
    }

    private static final class ResultSetToListOfBooks implements ResultSetExtractor<List<Book>> {
        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            try {
                rs.first();
                var listOfBooks = new ArrayList<Book>();
                do {
                    UUID currentBookID = UUID.fromString(rs.getString("book_id"));

                    Publisher publisher = Publisher.builder()
                            .id(UUID.fromString(rs.getString("publisher_id")))
                            .publisherName(new PublisherName(rs.getString("publisher_name")))
                            .address(new Address(
                                            rs.getString("publisher_state"),
                                            rs.getString("publisher_city"),
                                            rs.getString("publisher_street"),
                                            rs.getString("publisher_home")
                                    )
                            )
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

                    Set<Author> authors = new LinkedHashSet<>();
                    do {
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
                                        )
                                )
                                .events(new Events(
                                                rs.getObject("author_creation_date", Timestamp.class).toLocalDateTime(),
                                                rs.getObject("author_last_modified_date", Timestamp.class).toLocalDateTime()
                                        )
                                )
                                .build();
                        authors.add(author);
                    } while (
                            rs.next() && UUID.fromString(rs.getString("book_id")).equals(currentBookID)
                    );
                    rs.previous();

                    Book book = Book.builder()
                            .id(currentBookID)
                            .title(new Title(rs.getString("book_title")))
                            .description(new Description(rs.getString("book_description")))
                            .isbn(new ISBN(rs.getString("book_isbn")))
                            .price(new Price(rs.getDouble("book_price")))
                            .quantityOnHand(new QuantityOnHand(rs.getInt("book_quantity")))
                            .category(Category.valueOf(rs.getString("book_category")))
                            .events(new Events(
                                            rs.getObject("book_creation_date", Timestamp.class).toLocalDateTime(),
                                            rs.getObject("book_last_modified_date", Timestamp.class).toLocalDateTime()
                                    )
                            )
                            .publisher(publisher)
                            .authors(authors)
                            .build();

                    listOfBooks.add(book);
                } while (rs.next());
                return listOfBooks;
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
    }
}
