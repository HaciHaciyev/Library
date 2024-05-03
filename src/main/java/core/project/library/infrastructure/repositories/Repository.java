package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public class Repository {

    private final Optional<JdbcTemplate> jdbcTemplate;

    public Repository(Optional<JdbcTemplate> jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer count() {
        return jdbcTemplate.map(jdbc -> jdbc.queryForObject("Select COUNT(id) from Book", Integer.class))
                .orElse(-1);
    }

    public void bootstrap() {
        if (jdbcTemplate.isEmpty()) return;

        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Publisher"))
                .address(new Address("State", "City", "Street", "Home"))
                .phone(new Phone("11122-333-44-55"))
                .email(new Email("email@gmail.com"))
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .books(new HashSet<>())
                .build();

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Author"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .books(new HashSet<>())
                .build();

        Book book = Book.builder()
                .id(UUID.fromString("d4f0aa27-317b-4e00-9462-9a7f0faa7a5e"))
                .title(new Title("Title"))
                .description(new Description("Description"))
                .isbn(new ISBN("978-161-729-045-9"))
                .price(new BigDecimal("12.99"))
                .quantityOnHand(43)
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .category(Category.Adventure)
                .authors(new HashSet<>())
                .orders(new HashSet<>())
                .build();

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(1)
                .totalPrice(new TotalPrice(new BigDecimal("12.99")))
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .books(new HashSet<>())
                .build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Customer"))
                .lastName(new LastName("Customerovich"))
                .password(new Password("password"))
                .email(new Email("customer@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events(LocalDateTime.now(), LocalDateTime.now()))
                .orders(new HashSet<>())
                .build();

        book.addAuthor(author);
        book.addPublisher(publisher);
        customer.addOrder(order);
        book.addOrder(order);

        jdbcTemplate.get().update("""
        Insert into Book (id, title, description, isbn, price,
                  quantity_on_hand, category, created_date, last_modified_date)
                  values (?,?,?,?,?,?,?,?,?)
        """,
                book.getId().toString(), book.getTitle().title(), book.getDescription().description(),
                book.getIsbn().isbn(), book.getPrice(), book.getQuantityOnHand(), book.getCategory().toString(),
                book.getEvents().creation_date(), book.getEvents().last_update_date()
        );

        jdbcTemplate.get().update("""
        Insert into Author (id, first_name, last_name, email,
                    state, city, street, home, created_date, last_modified_date)
                    values (?,?,?,?,?,?,?,?,?,?)
        """,
                author.getId().toString(), author.getFirstName().firstName(), author.getLastName().lastName(),
                author.getEmail().email(), author.getAddress().state(), author.getAddress().city(),
                author.getAddress().street(), author.getAddress().home(),
                author.getEvents().creation_date(), author.getEvents().last_update_date()
        );

        jdbcTemplate.get().update("""
        Insert into Book_Author (id, book_id, author_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                book.getId().toString(),
                author.getId().toString()
        );

        jdbcTemplate.get().update("""
        Insert into Publisher (id, publisher_name, state, city, street, home,
                       phone, email, creation_date, last_modified_date)
                       values (?,?,?,?,?,?,?,?,?,?)
        """,
                publisher.getId().toString(), publisher.getPublisherName().publisherName(),
                publisher.getAddress().state(), publisher.getAddress().city(),
                publisher.getAddress().street(), publisher.getAddress().home(),
                publisher.getPhone().phoneNumber(), publisher.getEmail().email(),
                publisher.getEvents().creation_date(), publisher.getEvents().last_update_date()
        );

        jdbcTemplate.get().update("""
        Insert into Book_Publisher (id, book_id, publisher_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                book.getId().toString(),
                publisher.getId().toString()
        );

        jdbcTemplate.get().update("""
        Insert into Order_Line (id, count_of_book, total_price,
                        creation_date, last_modified_date)
                        values (?,?,?,?,?)
        """,
                order.getId().toString(), order.getCountOfBooks(), order.getTotalPrice().totalPrice(),
                order.getEvents().creation_date(), order.getEvents().last_update_date()
        );

        jdbcTemplate.get().update("""
        Insert into Book_Order (id, book_id, order_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                book.getId().toString(),
                order.getId().toString()
        );

        jdbcTemplate.get().update("""
        Insert into Customer (id, first_name, last_name, email, password,
                      state, city, street, home,
                      creation_date, last_modified_date)
                      values (?,?,?,?,?,?,?,?,?,?,?)
        """,
                customer.getId().toString(), customer.getFirstName().firstName(), customer.getLastName().lastName(),
                customer.getEmail().email(), customer.getPassword().password(), customer.getAddress().state(),
                customer.getAddress().city(), customer.getAddress().street(), customer.getAddress().home(),
                customer.getEvents().creation_date(), customer.getEvents().last_update_date()
        );

        jdbcTemplate.get().update("""
        Insert into Customer_Order (id, customer_id, order_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                customer.getId().toString(),
                order.getId().toString()
        );
    }
}
