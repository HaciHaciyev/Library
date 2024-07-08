package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.utilities.Result;
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

    public Result<Order, Exception> findById(UUID orderId) {
        try {
            Order order = getOrderById(orderId);

            return Result.success(order);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(e);
        }
    }

    public Result<List<Order>, Exception> findByCustomerId(UUID customerId) {
        try {
            String findByCustomerId = """
                    SELECT Orders.id FROM Orders
                    WHERE Orders.customer_id = ?
                    """;

            Set<UUID> orderIds = jdbcClient.sql(findByCustomerId)
                    .param(customerId.toString())
                    .query((rs, _) -> UUID.fromString(rs.getString("id")))
                    .set();

            List<Order> orders = orderIds.stream()
                    .map(this::getOrderById)
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

            Set<UUID> bookIds = jdbcClient.sql(findByBookId)
                    .param(bookId.toString())
                    .query((rs, _) -> UUID.fromString(rs.getString("id")))
                    .set();

            List<Order> orders = bookIds.stream()
                    .map(this::getOrderById)
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


            order.getBooks().forEach((book, countOfBooks) -> {
                jdbcClient.sql("""
                        INSERT INTO Book_Order (book_id, order_id, count_of_book_copies)
                                    VALUES (?, ?, ?)
                        """)
                        .param(book.getId().toString())
                        .param(order.getId().toString())
                        .param(countOfBooks)
                        .update();

                jdbcClient.sql("""
                        UPDATE Books SET quantity_on_hand = ?
                        WHERE id = ?
                        """)
                        .param(book.getQuantityOnHand().quantityOnHand())
                        .param(book.getId().toString())
                        .update();
            });

            return Result.success(order);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Result.failure(e);
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

                    return Order.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .paidAmount(new PaidAmount(rs.getDouble("paid_amount")))
                            .creditCard(creditCard)
                            .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
                            .customer(customer)
                            .books(bookMap)
                            .build();
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

                    return Customer.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .firstName(new FirstName(rs.getString("first_name")))
                            .lastName(new LastName(rs.getString("last_name")))
                            .email(new Email(rs.getString("email")))
                            .password(new Password(rs.getString("password")))
                            .address(address)
                            .events(events).build();
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
                    b.last_modified_date AS last_modified_date
                
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

                    Book book = Book.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .title(new Title(rs.getString("title")))
                            .description(new Description(rs.getString("description")))
                            .isbn(new ISBN(rs.getString("isbn")))
                            .price(new Price(rs.getDouble("price")))
                            .quantityOnHand(new QuantityOnHand(rs.getInt("quantity_on_hand")))
                            .category(Category.valueOf(rs.getString("category")))
                            .publisher(publisher)
                            .authors(authors)
                            .events(events)
                            .build();

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

                    return Author.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .firstName(new FirstName(rs.getString("first_name")))
                            .lastName(new LastName(rs.getString("last_name")))
                            .email(new Email(rs.getString("email")))
                            .address(address)
                            .events(events)
                            .build();
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

                    return Publisher.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .publisherName(new PublisherName(rs.getString("publisher_name")))
                            .address(address)
                            .phone(new Phone(rs.getString("phone")))
                            .email(new Email(rs.getString("email")))
                            .events(events)
                            .build();

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