package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.*;
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
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Order> findById(UUID orderId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            connection -> connection.prepareStatement(
                                    String.format(sqlForGetOrder, orderId.toString()),
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY
                            ),
                            new RowToOrder()
                    ).getFirst()
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Order> findByCustomerId(UUID customerId) {
        try {
            var listOfOrders = new ArrayList<Order>();

            Set<UUID> ordersId = new HashSet<>(
                    jdbcTemplate.query(
                            String.format(sqlForOrdersIdByCustomerId, customerId.toString()),
                            (rs, rowNum) -> UUID.fromString(rs.getString("id"))
                    )
            );

            ordersId.forEach(id ->
                    listOfOrders.add(
                            jdbcTemplate.query(
                                    connection -> connection.prepareStatement(
                                            String.format(sqlForGetOrder, id),
                                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                                            ResultSet.CONCUR_READ_ONLY
                                    ),
                                    new RowToOrder()
                            ).getFirst()
                    )
            );
            return listOfOrders;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<Order> findByBookId(UUID bookId) {
        try {
            var listOfOrders = new ArrayList<Order>();

            Set<UUID> ordersId = new HashSet<>(
                    jdbcTemplate.query(
                            String.format(sqlForOrdersIdByBookId, bookId.toString()),
                            (rs, rowNum) -> UUID.fromString(rs.getString("id"))
                    )
            );

            ordersId.forEach(id ->
                    listOfOrders.add(
                            jdbcTemplate.query(
                                    connection -> connection.prepareStatement(
                                            String.format(sqlForGetOrder, id),
                                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                                            ResultSet.CONCUR_READ_ONLY
                                    ),
                                    new RowToOrder()
                            ).getFirst()
                    )
            );
            return listOfOrders;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    private static final String sqlForOrdersIdByCustomerId = """
            Select Orders.id From Orders
            Where Orders.customer_id = '%s'
            """;

    private static final String sqlForOrdersIdByBookId = """
            Select Orders.id From Orders
            JOIN Book_Order On Orders.id = Book_Order.order_id
            JOIN Books On Book_Order.book_id = Books.id
            WHERE Books.id = '%s'
            """;

    private static final String sqlForGetOrder = """
            Select
              o.id AS order_id,
              o.count_of_book AS order_count_of_book,
              o.total_price AS order_total_price,
              o.creation_date AS order_creation_date,
              o.last_modified_date AS order_last_modified_date,
            
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
            """;

    private static final class RowToOrder implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                var listOfBooks = new HashSet<Book>();
                int countOfRowsScrollForBooksOfOrder = 0;
                do {
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

                    int countOfRowsScrollForBookAuthors = 0;
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
                        countOfRowsScrollForBookAuthors++;
                    } while (rs.next());

                    while (countOfRowsScrollForBookAuthors != 0) {
                        rs.previous();
                        countOfRowsScrollForBookAuthors--;
                    }

                    Book book = Book.builder()
                            .id(UUID.fromString(rs.getString("book_id")))
                            .title(new Title(rs.getString("book_title")))
                            .description(new Description(rs.getString("book_description")))
                            .isbn(new ISBN(rs.getString("book_isbn")))
                            .price(new BigDecimal(rs.getString("book_price")))
                            .quantityOnHand(rs.getInt("book_quantity"))
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
                    countOfRowsScrollForBooksOfOrder++;
                } while (rs.next());

                while (countOfRowsScrollForBooksOfOrder != 0) {
                    rs.previous();
                    countOfRowsScrollForBooksOfOrder--;
                }

                Customer customer = Customer.builder()
                        .id(UUID.fromString(rs.getString("customer_id")))
                        .firstName(new FirstName(rs.getString("customer_first_name")))
                        .lastName(new LastName(rs.getString("customer_last_name")))
                        .password(new Password(rs.getString("customer_password")))
                        .email(new Email(rs.getString("customer_email")))
                        .address(new Address(
                                rs.getString("customer_state"),
                                rs.getString("customer_city"),
                                rs.getString("customer_street"),
                                rs.getString("customer_home")
                                )
                        )
                        .events(new Events(
                                rs.getObject("customer_creation_date", Timestamp.class).toLocalDateTime(),
                                rs.getObject("customer_last_modified_date", Timestamp.class).toLocalDateTime()
                                )
                        )
                        .build();

                return Order.builder()
                        .id(UUID.fromString(rs.getString("order_id")))
                        .countOfBooks(rs.getInt("order_count_of_book"))
                        .totalPrice(new TotalPrice(new BigDecimal(rs.getString("order_total_price"))))
                        .events(new Events(
                                rs.getObject("order_creation_date", Timestamp.class).toLocalDateTime(),
                                rs.getObject("order_last_modified_date", Timestamp.class).toLocalDateTime()
                                )
                        )
                        .customer(customer)
                        .books(listOfBooks)
                        .build();
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
    }
}
