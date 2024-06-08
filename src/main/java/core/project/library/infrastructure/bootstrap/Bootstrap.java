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

import static core.project.library.infrastructure.utilities.Domain.*;
import static core.project.library.infrastructure.utilities.ValueObjects.*;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private static final Faker faker = new Faker();

    private final int maxNumberOfAuthors = faker.number().numberBetween(1, 15);
    private final int maxNumberOfAuthorsPerBook = faker.number().numberBetween(1, 4);
    private final int maxNumberOfPublishers = faker.number().numberBetween(1, 5);
    private final int maxNumberOfCustomers = faker.number().numberBetween(1, 10);
    private final int maxNumberOfBooks = faker.number().numberBetween(1, 30);
    private final int maxNumberOfOrders = faker.number().numberBetween(1, 15);
    private final int maxCountOfBooksForOrder = faker.number().numberBetween(1, 10);

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
        if (repository.count() < 1) {
            publishers.forEach(repository::savePublisher);
            authors.forEach(repository::saveAuthor);
            books.forEach(book -> {
                repository.saveBook(book);
                book.getAuthors().forEach(author -> repository.saveBookAuthor(book, author));
            });
            customers.forEach(repository::saveCustomer);
            orders.forEach(order -> {
                repository.saveOrder(order);
                order.getBooks().forEach(book -> repository.saveBookOrder(book, order));
            });
            log.info("Bootstrap is completed basic values in database.");
        }
    }

    private Supplier<Book> bookSupplier() {
        return () -> {
            double randomPrice = faker.number().randomDouble(2, 1, 100);
            int randomQuantity = faker.number().numberBetween(1, 15);
            int randomPublisher = faker.number().numberBetween(0, maxNumberOfPublishers);
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

    private Supplier<Order> orderSupplier() {
        return () -> {
            int randomCustomer = faker.number().numberBetween(0, customers.size());
            int countOfBooks = 1;

            if (maxCountOfBooksForOrder > 1) {
                countOfBooks = faker.number().numberBetween(1, maxCountOfBooksForOrder);
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
        var publisherSupplier = publisher();
        publishers = Stream.generate(publisherSupplier)
                .limit(maxNumberOfPublishers)
                .toList();
    }

    private void populateAuthors() {
        var authorSupplier = (Supplier<Author>) () -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(randomFirstName())
                .lastName(randomLastName())
                .email(randomEmail())
                .address(randomAddress())
                .events(new Events())
                .build();
        authors = Stream.generate(authorSupplier)
                .limit(maxNumberOfAuthors)
                .toList();
    }

    private void populateBooks() {
        var bookSupplier = bookSupplier();
        books = Stream.generate(bookSupplier)
                .limit(maxNumberOfBooks)
                .toList();
    }

    private void populateCustomers() {
        var customerSupplier = customer();
        customers = Stream.generate(customerSupplier)
                .limit(maxNumberOfCustomers)
                .toList();
    }

    private void populateOrders() {
        var orderSupplier = orderSupplier();
        orders = Stream.generate(orderSupplier)
                .limit(maxNumberOfOrders)
                .toList();
    }

    private Set<Book> getBooksForOrder(int countOfBooks) {
        return Stream.generate(() -> {
            int randomBook = faker.number().numberBetween(0, maxNumberOfBooks);
            return books.get(randomBook);
        }).limit(countOfBooks).collect(Collectors.toSet());
    }

    private Set<Author> getAuthorsForBook() {
        int numberOfAuthors = 1;

        if (maxNumberOfAuthorsPerBook > 1) {
            numberOfAuthors = faker.number().numberBetween(1, maxNumberOfAuthorsPerBook);
        }

        return Stream.generate(() -> {
            int randomAuthor = faker.number().numberBetween(0, maxNumberOfAuthors);
            return authors.get(randomAuthor);
        }).limit(numberOfAuthors).collect(Collectors.toSet());
    }
}