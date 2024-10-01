package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
public class OrderRepository {

    private final JdbcClient jdbcClient;

    public OrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Order> findById(UUID orderId) {
        try {
            Order order = getOrderById(orderId);

            return Optional.of(order);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public List<Order> findByCustomerId(UUID customerId) {
        try {
            String findByCustomerId = """
                    SELECT Orders.id FROM Orders
                    WHERE Orders.customer_id = ?
                    """;

            Set<UUID> orderIds = jdbcClient.sql(findByCustomerId)
                    .param(customerId.toString())
                    .query((rs, _) -> UUID.fromString(rs.getString("id")))
                    .set();

            return orderIds.stream()
                    .map(this::getOrderById)
                    .toList();
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Order> findByBookId(UUID bookId) {
        try {
            String findByBookId = """
                    Select Orders.id FROM Orders
                    JOIN Book_Order ON Orders.id = Book_Order.order_id
                    JOIN Books ON Book_Order.book_id = Books.id
                    WHERE Books.id = ?
                    """;

            Set<UUID> bookIds = jdbcClient.sql(findByBookId)
                    .param(bookId.toString())
                    .query((rs, _) -> UUID.fromString(rs.getString("id")))
                    .set();

            return bookIds.stream()
                    .map(this::getOrderById)
                    .toList();
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public Optional<Order> save(Order order, Set<Book> books) {
        try {
            String save = """
                    INSERT INTO Orders (id, customer_id,
                                    count_of_book, total_price,
                                    paid_amount, change_of_order,
                                    credit_card_number, credit_card_expiration, creation_date)
                                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            jdbcClient.sql(save)
                    .params(order.getId().toString(), order.getCustomer().getId(), order.getCountOfBooks(),
                            order.getTotalPrice().totalPrice(), order.getPaidAmount().paidAmount(),
                            order.getChangeOfOrder().changeOfOrder(), order.getCreditCard().creditCardNumber(),
                            order.getCreditCard().creditCardExpiration(), order.getCreationDate())
                    .update();


            order.getBooks().forEach((book, countOfBooks) -> jdbcClient.sql("""
                    INSERT INTO Book_Order (book_id, order_id, count_of_book_copies)
                                VALUES (?, ?, ?)
                    """)
                    .param(book.getId().toString())
                    .param(order.getId().toString())
                    .param(countOfBooks)
                    .update());

            books.forEach(book -> jdbcClient.sql("""
                        UPDATE Books SET quantity_on_hand = ?
                        WHERE id = ?
                        """)
                    .param(book.getQuantityOnHand().quantityOnHand())
                    .param(book.getId().toString())
                    .update());

            return Optional.of(order);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Order getOrderById(UUID orderId) {
        var bookIds = fetchBookIds(orderId);

        Map<Book, Integer> bookMap = new HashMap<>();
        for (UUID bookId : bookIds) {
            var publisher = fetchPublisher(bookId);
            var authors = fetchAuthors(bookId);
            var bookCount = fetchBooks(orderId, bookId, publisher, authors);
            bookMap.put(bookCount.book(), bookCount.count());
        }

        var customer = fetchCustomer(orderId);

        return fetchOrder(orderId, customer, bookMap);
    }

    private Order fetchOrder(UUID orderId, Customer customer, Map<Book, Integer> bookMap) {
        String sqlOrder = """
                SELECT
                    o.id AS id,
                    o.count_of_book AS count_of_book,
                    o.total_price AS total_price,
                    o.paid_amount AS paid_amount,
                    o.change_of_order AS change_of_order,
                    o.credit_card_number AS credit_card_number,
                    o.credit_card_expiration AS credit_card_expiration,
                    o.creation_date AS creation_date
                FROM Orders o WHERE id = ?
                """;

        return jdbcClient.sql(sqlOrder)
                .param(orderId.toString())
                .query((rs, _) -> {
                    CreditCard creditCard = new CreditCard(
                            rs.getString("credit_card_number"),
                            LocalDate.parse(rs.getString("credit_card_expiration"))
                    );

                    return Order.create(
                            UUID.fromString(rs.getString("id")),
                            new PaidAmount(rs.getDouble("paid_amount")),
                            creditCard,
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            customer,
                            bookMap
                    );
                }).single();
    }

    private Customer fetchCustomer(UUID orderId) {
        String sqlCustomer = """
                SELECT * FROM Customers c
                    INNER JOIN Orders o ON o.customer_id = c.id
                WHERE o.id = ?
                """;

        return jdbcClient.sql(sqlCustomer)
                .param(orderId.toString())
                .query((rs, _) -> {
                    Address address = new Address(
                            rs.getString("state"),
                            rs.getString("city"),
                            rs.getString("street"),
                            rs.getString("home")
                    );

                    Events events = new Events(
                            rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                            rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                    );

                    return Customer.create(
                            UUID.fromString(rs.getString("id")),
                            new FirstName(rs.getString("first_name")),
                            new LastName(rs.getString("last_name")),
                            new Password(rs.getString("password")),
                            new Email(rs.getString("email")),
                            address,
                            events
                    );
                }).single();
    }

    record BookCount(Book book, Integer count) {}

    private BookCount fetchBooks(UUID orderId, UUID bookId, Publisher publisher, Set<Author> authors) {
        String bookSql = """
                SELECT
                    b.id AS id,
                    b.title AS title,
                    b.description AS description,
                    b.isbn AS isbn,
                    b.price AS price,
                    b.quantity_on_hand AS quantity_on_hand,
                    bo.count_of_book_copies AS count_of_copies,
                    b.category AS category,
                    b.creation_date AS creation_date,
                    b.last_modified_date AS last_modified_date,
                    b.withdrawn_from_sale AS withdrawn_from_sale
                
                FROM Books b
                     INNER JOIN Book_Order bo ON b.id = bo.book_id AND bo.order_id = ?
                WHERE b.id = ?
                """;

        return jdbcClient.sql(bookSql)
                .param(orderId.toString())
                .param(bookId.toString())
                .query((rs, rowNum) -> {
                    Events events = new Events(
                            rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                            rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                    );

                    Book book = Book.create(
                            UUID.fromString(rs.getString("id")),
                            new Title(rs.getString("title")),
                            new Description(rs.getString("description")),
                            new ISBN(rs.getString("isbn")),
                            new Price(rs.getDouble("price")),
                            new QuantityOnHand(rs.getInt("quantity_on_hand")),
                            Category.valueOf(rs.getString("category")),
                            events,
                            rs.getBoolean("withdrawn_from_sale"),
                            publisher,
                            authors
                    );

                    return new BookCount(book, rs.getInt("count_of_copies"));
                }).single();
    }

    private Set<Author> fetchAuthors(UUID id) {
        String sqlAuthors = """
                Select * FROM Authors
                     INNER JOIN Book_Author ba ON Authors.id = ba.author_id
                WHERE ba.book_id = ?
                """;

        return jdbcClient.sql(sqlAuthors)
                .param(id.toString())
                .query((rs, _) -> {
                    Address address = new Address(
                            rs.getString("state"),
                            rs.getString("city"),
                            rs.getString("street"),
                            rs.getString("home")
                    );

                    Events events = new Events(
                            rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                            rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                    );

                    return Author.create(
                            UUID.fromString(rs.getString("id")),
                            new FirstName(rs.getString("first_name")),
                            new LastName(rs.getString("last_name")),
                            new Email(rs.getString("email")),
                            address,
                            events
                    );
                }).set();
    }

    private Publisher fetchPublisher(UUID id) {
        String sqlPublisher = """
                Select * FROM Publishers
                      INNER JOIN Books b ON Publishers.id = b.publisher_id
                WHERE b.id = ?
                """;

        return jdbcClient.sql(sqlPublisher)
                .param(id.toString())
                .query((rs, _) -> {
                    Address address = new Address(
                            rs.getString("state"),
                            rs.getString("city"),
                            rs.getString("street"),
                            rs.getString("home")
                    );

                    Events events = new Events(
                            rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                            rs.getObject("last_modified_date", Timestamp.class).toLocalDateTime()
                    );

                    return Publisher.create(
                            UUID.fromString(rs.getString("id")),
                            new PublisherName(rs.getString("publisher_name")),
                            address,
                            new Phone(rs.getString("phone")),
                            new Email(rs.getString("email")),
                            events
                    );
                }).single();
    }

    private List<UUID> fetchBookIds(UUID orderId) {
        String sqlBookId = """
                SELECT b.id
                FROM orders o
                         INNER JOIN Book_Order bo ON o.id = bo.order_id
                         INNER JOIN Books b ON bo.book_id = b.id
                WHERE o.id = ?
                """;

        return jdbcClient.sql(sqlBookId)
                .param(orderId.toString())
                .query((rs, _) -> UUID.fromString(rs.getString("id")))
                .list();
    }
}