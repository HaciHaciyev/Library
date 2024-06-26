package core.project.library;

import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DomainProviders {

    private DomainProviders() {}

    public static final Faker faker = new Faker();

    public static Supplier<Book> book() {
        return () -> {
            double randomPrice = faker.number().randomDouble(2, 1, 100);
            int randomQuantity = faker.number().numberBetween(1, 15);
            int countOfAuthors = faker.number().numberBetween(1, 5);

            Set<Author> authors = Stream.generate(() -> Author.builder()
                            .id(UUID.randomUUID())
                            .firstName(Bootstrap.randomFirstName())
                            .lastName(Bootstrap.randomLastName())
                            .email(Bootstrap.randomEmail())
                            .address(Bootstrap.randomAddress())
                            .events(new Events())
                            .build())
                    .limit(countOfAuthors)
                    .collect(Collectors.toSet());

            return Book.builder()
                    .id(UUID.randomUUID())
                    .title(Bootstrap.randomTitle())
                    .description(Bootstrap.randomDescription())
                    .isbn(Bootstrap.randomISBN13())
                    .price(BigDecimal.valueOf(randomPrice))
                    .quantityOnHand(randomQuantity)
                    .events(new Events())
                    .category(Bootstrap.randomCategory())
                    .publisher(Bootstrap.publisherFactory().get())
                    .authors(authors)
                    .build();
        };
    }

    // candidates for test utility
    public static Supplier<Order> order() {
        int countOfBooks = faker.number().numberBetween(1, 100);
        Set<Book> books = Stream.generate(book()).limit(countOfBooks).collect(Collectors.toSet());

        return () -> Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(countOfBooks)
                .totalPrice(Bootstrap.randomTotalPrice())
                .events(new Events())
                .customer(Bootstrap.customerFactory().get())
                .books(books)
                .build();
    }

    public static Supplier<Order> order(Set<Book> books, Customer customer) {
        int countOfBooks = faker.number().numberBetween(1, 100);

        return () -> Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(countOfBooks)
                .totalPrice(Bootstrap.randomTotalPrice())
                .events(new Events())
                .customer(customer)
                .books(books)
                .build();
    }
}
