package core.project.library.infrastructure.bootstrap;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.repository.BootstrapRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.project.library.infrastructure.utilities.ValueObjects.*;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private static final Faker faker = new Faker();

    private final int MAX_NUMBER_OF_AUTHORS = faker.number().numberBetween(1, 15);
    private final int MAX_NUMBER_OF_AUTHORS_PER_BOOK = faker.number().numberBetween(1, 4);
    private final int MAX_NUMBER_OF_PUBLISHERS = faker.number().numberBetween(1, 5);
    private final int MAX_NUMBER_OF_CUSTOMERS = faker.number().numberBetween(1, 10);
    private final int MAX_NUMBER_OF_BOOKS = faker.number().numberBetween(1, 30);
    private final int MAX_NUMBER_OF_ORDERS = faker.number().numberBetween(1, 15);
    private final int MAX_COUNT_OF_BOOKS_FOR_ORDER = faker.number().numberBetween(1, 10);

    private List<Publisher> publishers;
    private List<Author> authors;
    private List<Book> books;
    private List<Customer> customers;
    private List<Order> orders;

    private final BootstrapRepository repository;

    public Bootstrap(BootstrapRepository repository) {
        populatePublishers();
        populateAuthors();
        populateBooks();
        populateCustomers();
        populateOrders();
        this.repository = repository;
    }

    @Override
    public final void run(String... args) throws Exception {

    }

    private Supplier<Publisher> publisherSupplier() {
        return () -> Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(randomPublisherName())
                .address(randomAddress())
                .phone(randomPhone())
                .email(randomEmail())
                .events(new Events())
                .build();
    }

    private Supplier<Author> authorSupplier() {
        return () -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(randomFirstName())
                .lastName(randomLastName())
                .email(randomEmail())
                .address(randomAddress())
                .events(new Events())
                .build();
    }

    private Supplier<Book> bookSupplier() {
        return () -> {
            double randomPrice = faker.number().randomDouble(2, 1, 100);
            int randomQuantity = faker.number().numberBetween(1, 15);
            int randomPublisher = faker.number().numberBetween(0, MAX_NUMBER_OF_PUBLISHERS);
            Set<Author> authorsForBook = getAuthorsForBook();

            return Book.builder()
                    .id(UUID.randomUUID())
                    .title(randomTitle())
                    .description(randomDescription())
                    .isbn(randomISBN13())
                    .price(BigDecimal.valueOf(randomPrice))
                    .quantityOnHand(randomQuantity)
                    .events(new Events())
                    .category(randomCategory())
                    .publisher(publishers.get(randomPublisher))
                    .authors(authorsForBook)
                    .build();
        };
    }

    private Supplier<Customer> customerSupplier() {
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

    private Supplier<Order> orderSupplier() {
        return () -> {
            int randomCustomer = faker.number().numberBetween(0, customers.size());
            int countOfBooks = 1;

            if (MAX_COUNT_OF_BOOKS_FOR_ORDER > 1) {
                countOfBooks = faker.number().numberBetween(1, MAX_COUNT_OF_BOOKS_FOR_ORDER);
            }

            return Order.builder()
                    .id(UUID.randomUUID())
                    .countOfBooks(countOfBooks)
                    .totalPrice(randomTotalPrice())
                    .events(new Events())
                    .customer(customers.get(randomCustomer))
                    .books(getBooksForOrder(countOfBooks))
                    .build();
        };
    }

    private void populatePublishers() {
        var publisherSupplier = publisherSupplier();
        publishers = Stream.generate(publisherSupplier)
                .limit(MAX_NUMBER_OF_PUBLISHERS)
                .toList();
    }

    private void populateAuthors() {
        var authorSupplier = authorSupplier();
        authors = Stream.generate(authorSupplier)
                .limit(MAX_NUMBER_OF_AUTHORS)
                .toList();
    }

    private void populateBooks() {
        var bookSupplier = bookSupplier();
        books = Stream.generate(bookSupplier)
                .limit(MAX_NUMBER_OF_BOOKS)
                .toList();
    }

    private void populateCustomers() {
        var customerSupplier = customerSupplier();
        customers = Stream.generate(customerSupplier)
                .limit(MAX_NUMBER_OF_CUSTOMERS)
                .toList();
    }

    private void populateOrders() {
        var orderSupplier = orderSupplier();
        orders = Stream.generate(orderSupplier)
                .limit(MAX_NUMBER_OF_ORDERS)
                .toList();
    }

    private List<Book> getBooksForOrder(int countOfBooks) {
        return Stream.generate(() -> {
            int randomBook = faker.number().numberBetween(0, MAX_NUMBER_OF_BOOKS);
            return books.get(randomBook);
        }).limit(countOfBooks).toList();
    }

    private Set<Author> getAuthorsForBook() {
        int numberOfAuthors = 1;

        if (MAX_NUMBER_OF_AUTHORS_PER_BOOK > 1) {
            numberOfAuthors = faker.number().numberBetween(1, MAX_NUMBER_OF_AUTHORS_PER_BOOK);
        }

        return Stream.generate(() -> {
            int randomAuthor = faker.number().numberBetween(0, MAX_NUMBER_OF_AUTHORS);
            return authors.get(randomAuthor);
        }).limit(numberOfAuthors).collect(Collectors.toSet());
    }
}