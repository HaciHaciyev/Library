package core.project.library.domain;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

@SpringBootTest
public class DomainTest {

    @Test
    void testDomain() {
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Publisher"))
                .address(new Address("State", "City", "Street", "Home"))
                .phone(new Phone("11122-333-44-55"))
                .email(new Email("email@gmail.com"))
                .events(new Events())
                .books(new HashSet<>())
                .build();

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Author"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events())
                .books(new HashSet<>())
                .build();

        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(new Title("Title"))
                .description(new Description("Description"))
                .isbn(new ISBN("978-161-729-045-9"))
                .price(new BigDecimal("12.99"))
                .quantityOnHand(43)
                .events(new Events())
                .category(Category.Adventure)
                .authors(new HashSet<>())
                .orders(new HashSet<>())
                .build();

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(1)
                .totalPrice(new TotalPrice(new BigDecimal("12.99")))
                .events(new Events())
                .books(new HashSet<>())
                .build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Customer"))
                .lastName(new LastName("Customerovich"))
                .password(new Password("password"))
                .email(new Email("customer@gmail.com"))
                .events(new Events())
                .orders(new HashSet<>())
                .build();

        book.addAuthor(author);
        book.addPublisher(publisher);
        customer.addOrder(order);
        book.addOrder(order);

        System.out.println("Book: " + book);
        book.printPublisher();
        book.printAuthors();
        book.printOrders();
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Publisher: " + publisher);
        publisher.printBooks();
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Author: " + author);
        author.printBooks();
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Order: " + order);
        order.printCustomer();
        order.printBooks();
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Customer: " + customer);
        customer.printOrders();
    }
}
