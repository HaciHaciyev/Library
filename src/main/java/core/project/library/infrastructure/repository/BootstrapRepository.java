package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@org.springframework.stereotype.Repository
public class BootstrapRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final Address DEFAULT_ADDRESS =
            new Address("State", "City", "Street", "Home");

    private static final BigDecimal DEFAULT_PRICE =
            new BigDecimal("12.99");

    public BootstrapRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer count() {
        return jdbcTemplate.queryForObject("Select count(*) from Book", Integer.class);
    }

    public void bootstrap() {
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Publisher"))
                .address(DEFAULT_ADDRESS)
                .phone(new Phone("+994 50 1112233"))
                .email(new Email("email@gmail.com"))
                .events(new Events())
                .build();

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Author"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(DEFAULT_ADDRESS)
                .events(new Events())
                .build();

        Book book = Book.builder()
                .id(UUID.fromString("d4f0aa27-317b-4e00-9462-9a7f0faa7a5e"))
                .title(new Title("Title"))
                .description(new Description("Description"))
                .isbn(new ISBN("9781861972712"))
                .price(DEFAULT_PRICE)
                .quantityOnHand(43)
                .events(new Events())
                .category(Category.Adventure)
                .publisher(publisher)
                .authors(new HashSet<>(Collections.singleton(author)))
                .build();

        Customer customer = Customer.builder()
                .id(UUID.fromString("58e9909b-742f-4cd0-b1a1-0e8689d0fcfd"))
                .firstName(new FirstName("Customer"))
                .lastName(new LastName("Customerovich"))
                .password(new Password("password"))
                .email(new Email("customer@gmail.com"))
                .address(DEFAULT_ADDRESS)
                .events(new Events())
                .build();

        Order order = Order.builder()
                .id(UUID.fromString("a486f288-cec3-4205-b753-d4ddf2796f9a"))
                .countOfBooks(1)
                .totalPrice(new TotalPrice(DEFAULT_PRICE))
                .events(new Events())
                .customer(customer)
                .books(new HashSet<>(Collections.singleton(book)))
                .build();

        //-----------------------------------------------------------------------------------------------------------

        Publisher publisher2 = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Publisher2"))
                .address(DEFAULT_ADDRESS)
                .phone(new Phone("+994 50 1112233"))
                .email(new Email("email@gmail.com"))
                .events(new Events())
                .build();

        Author author2 = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Author2"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(DEFAULT_ADDRESS)
                .events(new Events())
                .build();

        Book book2 = Book.builder()
                .id(UUID.fromString("9f16fea8-4482-406c-aa53-43593d1cb480"))
                .title(new Title("Title2"))
                .description(new Description("Description"))
                .isbn(new ISBN("9781861972712"))
                .price(DEFAULT_PRICE)
                .quantityOnHand(43)
                .events(new Events())
                .category(Category.Adventure)
                .publisher(publisher)
                .authors(new HashSet<>(Collections.singleton(author2)))
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Customer2"))
                .lastName(new LastName("Customerovich2"))
                .password(new Password("password"))
                .email(new Email("customer2@gmail.com"))
                .address(DEFAULT_ADDRESS)
                .events(new Events())
                .build();

        Order order2 = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(1)
                .totalPrice(new TotalPrice(DEFAULT_PRICE))
                .events(new Events())
                .customer(customer)
                .books(new HashSet<>(Collections.singleton(book)))
                .build();

        //-----------------------------------------------------------------------------------------------------------

        savePublisher(publisher);
        savePublisher(publisher2);

        saveAuthor(author);
        saveAuthor(author2);

        saveBook(book);
        saveBook(book2);

        saveBookAuthor(book, author);
        saveBookAuthor(book2, author2);

        saveCustomer(customer);
        saveCustomer(customer2);

        saveOrder(order);
        saveOrder(order2);

        saveBookOrder(book, order);
        saveBookOrder(book2, order2);
    }

    private void savePublisher(Publisher publisher) {
        jdbcTemplate.update("""
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
    }

    private void saveAuthor(Author author) {
        jdbcTemplate.update("""
        Insert into Author (id, first_name, last_name, email,
                    state, city, street, home, created_date, last_modified_date)
                    values (?,?,?,?,?,?,?,?,?,?)
        """,
                author.getId().toString(), author.getFirstName().firstName(), author.getLastName().lastName(),
                author.getEmail().email(), author.getAddress().state(), author.getAddress().city(),
                author.getAddress().street(), author.getAddress().home(),
                author.getEvents().creation_date(), author.getEvents().last_update_date()
        );
    }

    private void saveBook(Book book) {
        jdbcTemplate.update("""
        Insert into Book (id, publisher_id, title, description, isbn, price,
                  quantity_on_hand, category, created_date, last_modified_date)
                  values (?,?,?,?,?,?,?,?,?,?)
        """,
                book.getId().toString(), book.getPublisher().getId().toString(),
                book.getTitle().title(), book.getDescription().description(),
                book.getIsbn().isbn(), book.getPrice(), book.getQuantityOnHand(),
                book.getCategory().toString(), book.getEvents().creation_date(),
                book.getEvents().last_update_date()
        );
    }

    private void saveBookAuthor(Book book, Author author) {
        jdbcTemplate.update("""
        Insert into Book_Author (id, book_id, author_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                book.getId().toString(),
                author.getId().toString()
        );
    }

    private void saveCustomer(Customer customer) {
        jdbcTemplate.update("""
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
    }

    private void saveOrder(Order order) {
        jdbcTemplate.update("""
        Insert into Order_Line (id, customer_id,
                        count_of_book, total_price,
                        creation_date, last_modified_date)
                        values (?,?,?,?,?,?)
        """,
                order.getId().toString(), order.getCustomer().getId().toString(),
                order.getCountOfBooks(), order.getTotalPrice().totalPrice(),
                order.getEvents().creation_date(), order.getEvents().last_update_date()
        );
    }

    private void saveBookOrder(Book book, Order order) {
        jdbcTemplate.update("""
        Insert into Book_Order (id, book_id, order_id)
                    values (?,?,?)
        """,
                UUID.randomUUID().toString(),
                book.getId().toString(),
                order.getId().toString()
        );
    }
}