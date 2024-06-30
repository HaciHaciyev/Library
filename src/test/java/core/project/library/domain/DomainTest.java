package core.project.library.domain;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static core.project.library.application.bootstrap.Bootstrap.randomCreditCard;

@Slf4j
class DomainTest {

    private static final Faker faker = new Faker();

    @Test
    @Disabled
    void testDomain() {
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(new PublisherName("Publisher"))
                .address(new Address("State", "City", "Street", "Home"))
                .phone(new Phone(faker.phoneNumber().phoneNumber()))
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
                .price(new Price(12.99))
                .quantityOnHand(new QuantityOnHand(43))
                .events(new Events())
                .category(Category.Adventure)
                .publisher(publisher)
                .authors(new HashSet<>(Collections.singleton(author)))
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

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(1)
                .paidAmount(new PaidAmount((double) faker.number().numberBetween(1, 5000)))
                .creditCard(randomCreditCard())
                .creationDate(LocalDateTime.now())
                .customer(customer)
                .books(Collections.singletonMap(book, 1))
                .build();

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
