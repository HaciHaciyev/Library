package core.project.library.infrastructure.utilities;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.project.library.infrastructure.utilities.ValueObjects.*;

public class Domain {

    private static final Faker faker = new Faker();

    public static Supplier<Author> author() {
                return () -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(randomFirstName())
                .lastName(randomLastName())
                .email(randomEmail())
                .address(randomAddress())
                .events(new Events())
                .build();
    }

    public static Supplier<Book> book() {
        return () -> {
            double randomPrice = faker.number().randomDouble(2, 1, 100);
            int randomQuantity = faker.number().numberBetween(1, 15);
            int countOfAuthors = faker.number().numberBetween(1, 5);

            Set<Author> authors = Stream.generate(() -> Author.builder()
                            .id(UUID.randomUUID())
                            .firstName(randomFirstName())
                            .lastName(randomLastName())
                            .email(randomEmail())
                            .address(randomAddress())
                            .events(new Events())
                            .build())
                    .limit(countOfAuthors)
                    .collect(Collectors.toSet());

            return Book.builder()
                    .id(UUID.randomUUID())
                    .title(randomTitle())
                    .description(randomDescription())
                    .isbn(randomISBN13())
                    .price(BigDecimal.valueOf(randomPrice))
                    .quantityOnHand(randomQuantity)
                    .events(new Events())
                    .category(randomCategory())
                    .publisher(publisher().get())
                    .authors(authors)
                    .build();
        };
    }

    public static Supplier<Book> book(Publisher publisher, Set<Author> authors) {
        return () -> {
            double randomPrice = faker.number().randomDouble(2, 1, 100);
            int randomQuantity = faker.number().numberBetween(1, 15);

            return Book.builder()
                    .id(UUID.randomUUID())
                    .title(randomTitle())
                    .description(randomDescription())
                    .isbn(randomISBN13())
                    .price(BigDecimal.valueOf(randomPrice))
                    .quantityOnHand(randomQuantity)
                    .events(new Events())
                    .category(randomCategory())
                    .publisher(publisher)
                    .authors(authors)
                    .build();
        };
    }

    public static Supplier<Customer> customer() {
        return () -> Customer.builder()
                .id(UUID.randomUUID())
                .firstName(randomFirstName())
                .lastName(randomLastName())
                .password(randomPassword())
                .email(randomEmail())
                .address(randomAddress())
                .events(new Events())
                .build();
    }

    public static Supplier<Order> order() {
        int countOfBooks = faker.number().numberBetween(1, 100);
        Set<Book> books = Stream.generate(book()).limit(countOfBooks).collect(Collectors.toSet());

        return () -> Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(countOfBooks)
                .totalPrice(randomTotalPrice())
                .events(new Events())
                .customer(customer().get())
                .books(books)
                .build();
    }

    public static Supplier<Order> order(Set<Book> books, Customer customer) {
        int countOfBooks = faker.number().numberBetween(1, 100);

        return () -> Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(countOfBooks)
                .totalPrice(randomTotalPrice())
                .events(new Events())
                .customer(customer)
                .books(books)
                .build();
    }

    public static Supplier<Publisher> publisher() {
        return () -> Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(randomPublisherName())
                .address(randomAddress())
                .phone(randomPhone())
                .email(randomEmail())
                .events(new Events())
                .build();
    }
}
