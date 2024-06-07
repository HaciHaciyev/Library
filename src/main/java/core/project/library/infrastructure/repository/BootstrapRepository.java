package core.project.library.infrastructure.repository;

import core.project.library.domain.entities.*;
import org.springframework.jdbc.core.JdbcTemplate;

@org.springframework.stereotype.Repository
public class BootstrapRepository {

    private final JdbcTemplate jdbcTemplate;

    public BootstrapRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer count() {
        return jdbcTemplate.queryForObject("Select count(*) from Book", Integer.class);
    }

    private void savePublisher(Publisher publisher) {
        jdbcTemplate.update("""
                        Insert into Publishers (id, publisher_name, state, city, street, home,
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
                        Insert into Authors (id, first_name, last_name, email,
                                    state, city, street, home, creation_date, last_modified_date)
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
                        Insert into Books (id, publisher_id, title, description, isbn, price,
                                  quantity_on_hand, category, creation_date, last_modified_date)
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
                        Insert into Book_Author (book_id, author_id)
                                    values (?,?,?)
                        """,
                book.getId().toString(),
                author.getId().toString()
        );
    }

    private void saveCustomer(Customer customer) {
        jdbcTemplate.update("""
                        Insert into Customers (id, first_name, last_name, email, password,
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
                        Insert into Orders (id, customer_id,
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
                        Insert into Book_Order (book_id, order_id)
                                    values (?,?,?)
                        """,
                book.getId().toString(),
                order.getId().toString()
        );
    }
}