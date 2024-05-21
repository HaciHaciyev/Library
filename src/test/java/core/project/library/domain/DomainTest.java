package core.project.library.domain;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Slf4j
@SpringBootTest
class DomainTest {

    @Test
    void testDomain() {
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Publisher"))
                .address(new Address("State", "City", "Street", "Home"))
                .phone(new Phone("+994 50 1112233"))
                .email(new Email("email@gmail.com"))
                .events(new Events())
                .build();

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Author"))
                .lastName(new LastName("Authorovich"))
                .email(new Email("author@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events())
                .build();

        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(new Title("Title"))
                .description(new Description("Description"))
                .isbn(new ISBN("9781861972712"))
                .price(new BigDecimal("12.99"))
                .quantityOnHand(43)
                .events(new Events())
                .category(Category.Adventure)
                .publisher(publisher)
                .authors(new HashSet<>(Collections.singleton(author)))
                .orders(new HashSet<>())
                .build();

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(1)
                .totalPrice(new TotalPrice(new BigDecimal("12.99")))
                .events(new Events())
                .build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName("Customer"))
                .lastName(new LastName("Customerovich"))
                .password(new Password("password"))
                .email(new Email("customer@gmail.com"))
                .address(new Address("State", "City", "Street", "Home"))
                .events(new Events())
                .build();

        book.addOrder(order);
        customer.addOrder(order);

        System.out.println("Book: " + book);
        System.out.println(book.getPublisher());
        System.out.println(book.getAuthors());
        System.out.println(book.getOrders());
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Publisher: " + publisher);
        System.out.println(publisher.getBooks());
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Author: " + author);
        System.out.println(author.getBooks());
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Order: " + order);
        System.out.println(order.getCustomer());
        System.out.println(order.getBooks());
        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Customer: " + customer);
        System.out.println(customer.getOrders());
    }
}
