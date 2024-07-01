package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.handlers.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class OrderRepository {

    private static final String GET_ORDER_BY_ID = """
            SELECT
              o.id AS order_id,
              o.count_of_book AS order_count_of_book,
              o.total_price AS order_total_price,
              o.paid_amount AS order_paid_amount,
              o.change_of_order AS order_change_of_order,
              o.credit_card_number AS credit_card_number,
              o.credit_card_expiration AS credit_card_expiration,
              o.creation_date AS order_creation_date,
            
              c.id AS customer_id,
              c.first_name AS customer_first_name,
              c.last_name AS customer_last_name,
              c.email AS customer_email,
              c.password AS customer_password,
              c.state AS customer_state,
              c.city AS customer_city,
              c.street AS customer_street,
              c.home AS customer_home,
              c.creation_date AS customer_creation_date,
              c.last_modified_date AS customer_last_modified_date,
            
              b.id AS book_id,
              b.title AS book_title,
              b.description AS book_description,
              b.isbn AS book_isbn,
              b.price AS book_price,
              b.quantity_on_hand AS book_quantity,
              bo.count_of_book_copies AS book_count_of_copies,
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
            FROM Orders o
              INNER JOIN Customers c ON o.customer_id = c.id
              INNER JOIN Book_Order bo ON o.id = bo.order_id
              INNER JOIN Books b ON bo.book_id = b.id
              INNER JOIN Publishers p ON b.publisher_id = p.id
              INNER JOIN Book_Author ba ON b.id = ba.book_id
              INNER JOIN Authors a ON ba.author_id = a.id
            WHERE o.id = '%s'
            ORDER BY b.id
            """;

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private PreparedStatementCreator preparedStatementFactory(UUID id) {
        return connection -> connection.prepareStatement(
                GET_ORDER_BY_ID.formatted(id.toString()),
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
    }

    public Result<Order, Exception> findById(UUID orderId) {
        try {
            Order order = jdbcTemplate.query(
                    preparedStatementFactory(orderId), new OrderMapper()
            );

            return Result.success(order);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(new NotFoundException("Couldn't find order"));
        }
    }

    public Result<List<Order>, Exception> findByCustomerId(UUID customerId) {
        try {
            String findByCustomerId = """
                    SELECT Orders.id FROM Orders
                    WHERE Orders.customer_id = ?
                    """;

            List<Order> orders = jdbcTemplate.query(
                            findByCustomerId,
                            (rs, _) -> UUID.fromString(rs.getString("id")),
                            customerId.toString()
                    ).stream()
                    .distinct()
                    .map(uuid ->
                            jdbcTemplate.query(
                                    preparedStatementFactory(uuid),
                                    new OrderMapper()
                            )
                    )
                    .toList();

            return Result.success(orders);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(new NotFoundException("Couldn't find orders"));
        }
    }

    public Result<List<Order>, Exception> findByBookId(UUID bookId) {
        try {
            String findByBookId = """
                    Select Orders.id FROM Orders
                    JOIN Book_Order ON Orders.id = Book_Order.order_id
                    JOIN Books ON Book_Order.book_id = Books.id
                    WHERE Books.id = ?
                    """;

            List<Order> orders =
                    jdbcTemplate.query(findByBookId, (rs, _) -> UUID.fromString(rs.getString("id")), bookId.toString())
                            .stream()
                            .distinct()
                            .map(uuid -> jdbcTemplate.query(
                            preparedStatementFactory(uuid),
                            new OrderMapper()
                            ))
                    .toList();

            return Result.success(orders);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(new NotFoundException("Couldn't find orders"));
        }
    }

    @Transactional
    public Result<Order, Exception> save(Order order) {
        try {
            jdbcTemplate.update("""
                            INSERT INTO Orders (id, customer_id,
                                            count_of_book, total_price,
                                            paid_amount, change_of_order,
                                            credit_card_number, credit_card_expiration, creation_date)
                                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """,
                    order.getId().toString(), order.getCustomer().getId().toString(),
                    order.getCountOfBooks(), order.getTotalPrice().totalPrice(), order.getPaidAmount().paidAmount(),
                    order.getChangeOfOrder().changeOfOrder(), order.getCreditCard().creditCardNumber(),
                    order.getCreditCard().creditCardExpiration(), order.getCreationDate()
            );

            order.getBooks().forEach((book, countOfBooks) -> {
                jdbcTemplate.update("""
                                    INSERT INTO Book_Order (book_id, order_id, count_of_book_copies)
                                                VALUES (?, ?, ?)
                                    """,
                        book.getId().toString(),
                        order.getId().toString(),
                        countOfBooks
                );

                jdbcTemplate.update("""
                    UPDATE Books SET quantity_on_hand = ?
                    WHERE id = ?
                    """,
                        book.getQuantityOnHand().quantityOnHand(),
                        book.getId().toString()
                        );
            });

            return Result.success(order);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(e);
        }
    }

    private static class OrderMapper implements ResultSetExtractor<Order> {
        private record BookAndCount(Book book, Integer count) {}

        @Override
        public Order extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Book, Integer> books = new LinkedHashMap<>();
            String lastBookId = null;

            while (rs.next()) {
                String currentBookId = rs.getString("book_id");

                if (!currentBookId.equals(lastBookId)) {
                    Publisher publisher = mapPublisher(rs);
                    Set<Author> authors = collectAuthors(rs, currentBookId);

                    var bookPair = mapBook(rs, publisher, authors);
                    books.put(bookPair.book, bookPair.count);

                    lastBookId = currentBookId;
                }
            }

            rs.previous();
            Customer customer = mapCustomer(rs);
            return constructOrder(rs, customer, books);
        }

        private Set<Author> collectAuthors(ResultSet rs, String bookId) throws SQLException {
            Set<Author> authors = new LinkedHashSet<>();

            do {
                authors.add(mapAuthor(rs));
            } while (rs.next() && bookId.equals(rs.getString("book_id")));

            rs.previous();
            return authors;
        }

        private Order constructOrder(ResultSet rs, Customer customer, Map<Book, Integer> books) throws SQLException {
            CreditCard creditCard = new CreditCard(
                    rs.getString("order_credit_card_number"),
                    LocalDate.parse(rs.getString("order_credit_card_expiration"))
            );

            return Order.builder()
                    .id(UUID.fromString(rs.getString("order_id")))
                    .paidAmount(new PaidAmount(rs.getDouble("order_paid_amount")))
                    .creditCard(creditCard)
                    .creationDate(LocalDateTime.parse(rs.getString("order_creation_date")))
                    .books(books)
                    .customer(customer)
                    .build();
        }

        private Customer mapCustomer(ResultSet rs) throws SQLException {
            Address address = new Address(
                    rs.getString("customer_state"),
                    rs.getString("customer_city"),
                    rs.getString("customer_street"),
                    rs.getString("customer_home")
            );

            Events events = new Events(
                    rs.getObject("customer_creation_date", Timestamp.class).toLocalDateTime(),
                    rs.getObject("customer_last_modified_date", Timestamp.class).toLocalDateTime()
            );

            return Customer.builder()
                    .id(UUID.fromString(rs.getString("customer_id")))
                    .firstName(new FirstName(rs.getString("customer_first_name")))
                    .lastName(new LastName(rs.getString("customer_last_name")))
                    .password(new Password(rs.getString("customer_password")))
                    .email(new Email(rs.getString("customer_email")))
                    .address(address)
                    .events(events)
                    .build();
        }

        private BookAndCount mapBook(ResultSet rs, Publisher publisher, Set<Author> authors) throws SQLException {
            Events events = new Events(
                    rs.getObject("book_creation_date", Timestamp.class).toLocalDateTime(),
                    rs.getObject("book_last_modified_date", Timestamp.class).toLocalDateTime()
            );

            Book book = Book.builder()
                    .id(UUID.fromString(rs.getString("book_id")))
                    .title(new Title(rs.getString("book_title")))
                    .description(new Description(rs.getString("book_description")))
                    .isbn(new ISBN(rs.getString("book_isbn")))
                    .price(new Price(rs.getDouble("book_price")))
                    .quantityOnHand(new QuantityOnHand(rs.getInt("book_quantity")))
                    .category(Category.valueOf(rs.getString("book_category")))
                    .events(events)
                    .publisher(publisher)
                    .authors(authors)
                    .build();

            return new BookAndCount(book, rs.getInt("book_count"));
        }

        private Author mapAuthor(ResultSet rs) throws SQLException {
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

            return Author.builder()
                    .id(UUID.fromString(rs.getString("author_id")))
                    .firstName(new FirstName(rs.getString("author_first_name")))
                    .lastName(new LastName(rs.getString("author_last_name")))
                    .email(new Email(rs.getString("author_email")))
                    .address(address)
                    .events(events)
                    .build();
        }

        private Publisher mapPublisher(ResultSet rs) throws SQLException {
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

            return Publisher.builder()
                    .id(UUID.fromString(rs.getString("publisher_id")))
                    .publisherName(new PublisherName(rs.getString("publisher_name")))
                    .address(address)
                    .phone(new Phone(rs.getString("publisher_phone")))
                    .email(new Email(rs.getString("publisher_email")))
                    .events(events)
                    .build();
        }
    }
}
