package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
            String findISBN = "Select COUNT(isbn) from Books where isbn = ?";
            Integer count = jdbcTemplate.queryForObject(
                    findISBN,
                    Integer.class,
                    verifiableIsbn.isbn()
            );
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public Optional<Book> findById(UUID bookId) {
        try {
            String sqlQuery = String.format(SQL_FOR_GET_BOOK_BY_ID, bookId);

            return Optional.ofNullable(
                    jdbcTemplate.query(preparedStatementFactory(sqlQuery), this::extractDataToBook)
            );
        } catch (EmptyResultDataAccessException e) {
            log.info("BookRepository findById(...): {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Book> findByISBN(ISBN isbn) {
        try {
            String sqlQuery = String.format(SQL_FOR_GET_BOOK_BY_ISBN, isbn.isbn());

            return Optional.ofNullable(
                    jdbcTemplate.query(preparedStatementFactory(sqlQuery), this::extractDataToBook)
            );
        } catch (EmptyResultDataAccessException e) {
            log.info("BookRepository findByISBN(...): {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<Book> listOfBooks(
            Integer pageNumber, Integer pageSize, String title, String category
    ) {
        try {
            final int limit = buildLimit(pageSize);
            final int offSet = buildOffSet(limit, pageNumber);
            String sqlQuery = buildQueryForListOfBooks(limit, offSet, title, category);

            return jdbcTemplate.query(preparedStatementFactory(sqlQuery), this::extractDataToListOfBooks);
        } catch (EmptyResultDataAccessException e) {
            log.info("BookRepository listOfBooks(...): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
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
                book.getPrice().price(),
                book.getQuantityOnHand().quantityOnHand(),
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

    @Transactional
    public void patchBook(Book foundBook) {
        jdbcTemplate.update("""
            Update Books Set
                 description = ?,
                 price = ?,
                 quantity_on_hand = ?
            Where id = ?
            """,
                foundBook.getDescription().description(),
                foundBook.getPrice().price(),
                foundBook.getQuantityOnHand().quantityOnHand(),
                foundBook.getId().toString()
        );
    }

    public void withdrawBookFromTheSale(Book book) {
        jdbcTemplate.update("""
            Update Books Set
                  withdrawn_from_sale = ?
            Where id = ?
            """, book.getWithdrawnFromSale(), book.getId().toString()
        );
    }

    /**Below this are the auxiliary methods & fields.*/

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

    private String buildQueryForListOfBooks(int limit, int offSet, String title, String category) {
        String limitAndOffset = "LIMIT %s OFFSET %s";
        StringBuilder sqlQuery = new StringBuilder(
                SQL_FOR_GET_BOOK_BY_ID.replace("WHERE b.id = '%s'", "")
        );

        if (title != null && category != null) {
            sqlQuery.append("WHERE b.title = '%s' AND b.category = '%s'").append(limitAndOffset);
            return String.format(sqlQuery.toString(), title, category, limit, offSet);
        } else if (title != null) {
            sqlQuery.append("WHERE b.title = '%s'").append(limitAndOffset);
            return String.format(sqlQuery.toString(), title, limit, offSet);
        } else if (category != null) {
            sqlQuery.append("WHERE b.category = '%s'").append(limitAndOffset);
            return String.format(sqlQuery.toString(), category, limit, offSet);
        }

        return String.format(sqlQuery.append(limitAndOffset).toString(), limit, offSet);
    }

    public static final String SQL_FOR_GET_BOOK_BY_ID = """
                SELECT
                    b.id AS book_id,
                    b.title AS book_title,
                    b.description AS book_description,
                    b.isbn AS book_isbn,
                    b.price AS book_price,
                    b.quantity_on_hand AS book_quantity,
                    b.category AS book_category,
                    b.withdrawn_from_sale AS withdrawn_from_sale,
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

    public static final String SQL_FOR_GET_BOOK_BY_ISBN = SQL_FOR_GET_BOOK_BY_ID.replace(
            "WHERE b.id = '%s'", "WHERE b.isbn = '%s'"
    );

    private PreparedStatementCreator preparedStatementFactory(String sqlQuery) {
        return connection -> connection.prepareStatement(
                sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY
        );
    }

    public Book extractDataToBook(ResultSet rs) throws SQLException {
        rs.first();
        UUID currentBookId = currentBookId(rs);
        Publisher publisher = extractDataToPublisherOfBook(rs);
        Set<Author> authors = extractDataToListOfAuthorsOfBook(rs, currentBookId);

        return extractDataAndCompletelyConstructTheBook(rs, currentBookId, publisher, authors);
    }

    public List<Book> extractDataToListOfBooks(ResultSet rs) throws SQLException {
        rs.first();
        var listOfBooks = new ArrayList<Book>();

        do {
            UUID currentBookId = currentBookId(rs);
            Publisher publisher = extractDataToPublisherOfBook(rs);
            Set<Author> authors = extractDataToListOfAuthorsOfBook(rs, currentBookId);
            Book book = extractDataAndCompletelyConstructTheBook(rs, currentBookId, publisher, authors);
            listOfBooks.add(book);
        } while (rs.next());

        return listOfBooks;
    }

    private UUID currentBookId(ResultSet rs) throws SQLException {
        return UUID.fromString(rs.getString("book_id"));
    }

    private Publisher extractDataToPublisherOfBook(ResultSet rs) throws SQLException {
        Address address = new Address(
                rs.getString("publisher_state"),
                rs.getString("publisher_city"),
                rs.getString("publisher_street"),
                rs.getString("publisher_home")
        );

        Events events = new Events(
                rs.getObject("publisher_creation_date", Timestamp.class).toLocalDateTime(),
                rs.getObject("publisher_last_modified_date", Timestamp.class).toLocalDateTime()
        );

        return Publisher.create(
                UUID.fromString(rs.getString("publisher_id")),
                new PublisherName(rs.getString("publisher_name")),
                address,
                new Phone(rs.getString("publisher_phone")),
                new Email(rs.getString("publisher_email")),
                events
        );
    }

    private Set<Author> extractDataToListOfAuthorsOfBook(ResultSet rs, UUID currentBookID) throws SQLException {
        Set<Author> authors = new LinkedHashSet<>();
        do {
            Address address = new Address(
                    rs.getString("author_state"),
                    rs.getString("author_city"),
                    rs.getString("author_street"),
                    rs.getString("author_home")
            );
            Events events = new Events(
                    rs.getObject("author_creation_date", Timestamp.class).toLocalDateTime(),
                    rs.getObject("author_last_modified_date", Timestamp.class).toLocalDateTime()
            );

            Author author = Author.create(
                    UUID.fromString(rs.getString("id")),
                    new FirstName(rs.getString("first_name")),
                    new LastName(rs.getString("last_name")),
                    new Email(rs.getString("email")),
                    address,
                    events
            );

            authors.add(author);
        } while (
                rs.next() && UUID.fromString(rs.getString("book_id")).equals(currentBookID)
        );
        rs.previous();
        return authors;
    }

    private Book extractDataAndCompletelyConstructTheBook(
            ResultSet rs, UUID currentBookId, Publisher publisher, Set<Author> authors
    ) throws SQLException {

        Events events = new Events(
                rs.getObject("book_creation_date", Timestamp.class).toLocalDateTime(),
                rs.getObject("book_last_modified_date", Timestamp.class).toLocalDateTime()
        );

        return Book.create(
                currentBookId,
                new Title(rs.getString("book_title")),
                new Description(rs.getString("book_description")),
                new ISBN(rs.getString("book_isbn")),
                new Price(rs.getDouble("book_price")),
                new QuantityOnHand(rs.getInt("book_quantity")),
                Category.valueOf(rs.getString("book_category")),
                events,
                rs.getBoolean("withdrawn_from_sale"),
                publisher,
                authors
        );
    }
}
