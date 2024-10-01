package core.project.library;

import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.PaidAmount;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.project.library.application.bootstrap.Bootstrap.randomCreditCard;

public class DomainProviders {

    private DomainProviders() {}

    public static final Faker faker = new Faker();

    public static Supplier<Book> book() {
        return () -> {
            double randomPrice = faker.number().randomDouble(2, 1, 100);
            int randomQuantity = faker.number().numberBetween(1, 15);
            int countOfAuthors = faker.number().numberBetween(1, 5);

            Set<Author> authors = Stream.generate(() -> Author.create(
                            UUID.randomUUID(),
                            Bootstrap.randomFirstName(),
                            Bootstrap.randomLastName(),
                            Bootstrap.randomEmail(),
                            Bootstrap.randomAddress(),
                            new Events()
                    ))
                    .limit(countOfAuthors)
                    .collect(Collectors.toSet());

            return Book.create(
                    UUID.randomUUID(),
                    Bootstrap.randomTitle(),
                    Bootstrap.randomDescription(),
                    Bootstrap.randomISBN13(),
                    Bootstrap.randomPrice(),
                    Bootstrap.randomQuantityOnHand(),
                    Bootstrap.randomCategory(),
                    new Events(),
                    false,
                    Bootstrap.publisherFactory().get(),
                    authors
                    );
        };
    }

    /** candidates for test utility*/
    public static Supplier<Order> order() {
        int countOfBooks = faker.number().numberBetween(1, 100);
        Set<Book> books = Stream.generate(book()).limit(countOfBooks).collect(Collectors.toSet());

        return () -> Order.create(
                UUID.randomUUID(),
                new PaidAmount((double) faker.number().numberBetween(1, 5000)),
                randomCreditCard(),
                LocalDateTime.now(),
                Bootstrap.customerFactory().get(),
                new LinkedHashMap<>()
        );
    }

    public static Supplier<Order> order(Set<Book> books, Customer customer) {
        int countOfBooks = faker.number().numberBetween(1, 100);

        return () -> Order.create(
                UUID.randomUUID(),
                new PaidAmount((double) faker.number().numberBetween(1, 5000)),
                randomCreditCard(),
                LocalDateTime.now(),
                customer,
                new LinkedHashMap<>()
        );
    }
}
