package core.project.library.application.bootstrap;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.repository.*;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private static final Faker faker = new Faker();
    private static final Random random = new Random(System.currentTimeMillis());

    private static final int MAX_NUMBER_OF_AUTHORS = faker.number().numberBetween(5, 15);
    private static final int MAX_NUMBER_OF_AUTHORS_PER_BOOK = 4;
    private static final int MAX_NUMBER_OF_BOOKS = faker.number().numberBetween(5, 30);
    private static final int MAX_NUMBER_OF_BOOKS_PER_ORDER = faker.number().numberBetween(5, 10);
    private static final int MAX_NUMBER_OF_CUSTOMERS = faker.number().numberBetween(3, 10);
    private static final int MAX_NUMBER_OF_ORDERS = faker.number().numberBetween(5, 15);
    private static final int MAX_NUMBER_OF_PUBLISHERS = faker.number().numberBetween(2, 5);

    private static List<Publisher> publishers;
    private static List<Author> authors;
    private static List<Book> books;
    private static List<Customer> customers;
    private static List<Order> orders;

    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public Bootstrap(PublisherRepository publisherRepository, AuthorRepository authorRepository,
                     BookRepository bookRepository, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;

        populatePublishers();
        populateAuthors();
        populateBooks();
        populateCustomers();
        populateOrders();
    }

    @Override
    public final void run(String... args) {
        if (bookRepository.count() < 1) {
            publishers.forEach(publisherRepository::savePublisher);
            authors.forEach(authorRepository::saveAuthor);
            books.forEach(bookRepository::completelySaveBook);
            customers.forEach(customerRepository::saveCustomer);
            orders.forEach(orderRepository::save);
            log.info("Bootstrap is completed basic values in database.");
        }
    }

    private static void populatePublishers() {
        publishers = Stream.generate(publisherFactory())
                .limit(MAX_NUMBER_OF_PUBLISHERS)
                .toList();
    }

    private static void populateAuthors() {
        authors = Stream.generate(authorFactory())
                .limit(MAX_NUMBER_OF_AUTHORS)
                .distinct()
                .toList();
    }

    private static void populateBooks() {
        books = Stream.generate(bookFactory())
                .limit(MAX_NUMBER_OF_BOOKS)
                .toList();
    }

    private static void populateCustomers() {
        customers = Stream.generate(customerFactory())
                .limit(MAX_NUMBER_OF_CUSTOMERS)
                .toList();
    }

    private static void populateOrders() {
        orders = Stream.generate(orderFactory())
                .limit(MAX_NUMBER_OF_ORDERS)
                .toList();
    }

    public static Supplier<Publisher> publisherFactory() {
        return () -> Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(randomPublisherName())
                .address(randomAddress())
                .phone(randomPhone())
                .email(randomEmail())
                .events(new Events())
                .build();
    }

    public static Supplier<Author> authorFactory() {
        return () -> Author.builder()
                .id(UUID.randomUUID())
                .firstName(randomFirstName())
                .lastName(randomLastName())
                .email(randomEmail())
                .address(randomAddress())
                .events(new Events())
                .build();
    }

    public static Supplier<Book> bookFactory() {
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
                    .price(new Price(randomPrice))
                    .quantityOnHand(new QuantityOnHand(randomQuantity))
                    .events(new Events())
                    .category(randomCategory())
                    .publisher(publishers.get(randomPublisher))
                    .authors(authorsForBook)
                    .build();
        };
    }

    private static Set<Author> getAuthorsForBook() {
        int numberOfAuthors = faker.number().numberBetween(1, MAX_NUMBER_OF_AUTHORS_PER_BOOK);

        return authors.stream().limit(numberOfAuthors).collect(Collectors.toSet());
    }

    public static Supplier<Customer> customerFactory() {
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

    public static Supplier<Order> orderFactory() {
        return () -> {
            int randomCustomer = faker.number().numberBetween(0, customers.size());
            int countOfBooksPerOrder = faker.random().nextInt(1, MAX_NUMBER_OF_BOOKS_PER_ORDER);

            var booksForOrder = getBooksForOrder(countOfBooksPerOrder);

            return Order.builder()
                    .id(UUID.randomUUID())
                    .countOfBooks(countOfBooksPerOrder)
                    .paidAmount(new PaidAmount((double) faker.number().numberBetween(1, 5000)))
                    .creditCard(randomCreditCard())
                    .creationDate(LocalDateTime.now())
                    .customer(customers.get(randomCustomer))
                    .books(booksForOrder)
                    .build();
        };
    }

    private static Map<Book, Integer> getBooksForOrder(int countOfBooks) {
        return Stream.generate(() -> {
            int randomBook = faker.number().numberBetween(0, books.size());
            return books.get(randomBook);
        }).limit(countOfBooks).collect(Collectors.toMap(book -> book, _ -> 1, Integer::sum));
    }

    public static Address randomAddress() {
        return new Address(
                faker.address().state(),
                faker.address().city(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress()
        );
    }

    public static Category randomCategory() {
        int randomCategory = faker.number().numberBetween(0, Category.values().length);
        return Category.values()[randomCategory];
    }

    public static Description randomDescription() {
        return new Description(faker.text().text(
                15,
                255,
                true,
                true,
                true)
        );
    }

    public static Email randomEmail() {
        return new Email(faker.internet().emailAddress());
    }

    public static FirstName randomFirstName() {
        return new FirstName(faker.name().firstName());
    }

    public static ISBN randomISBN13() {
        StringBuilder isbn = new StringBuilder();
        isbn.append(ThreadLocalRandom.current().nextBoolean() ? "978" : "979");

        for (int i = 0; i < 9; i++) {
            isbn.append(ThreadLocalRandom.current().nextInt(10));
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        isbn.append(checkDigit);

        if (isIsbn13Valid(isbn.toString())) {
            return new ISBN(isbn.toString());
        } else {
            return randomISBN13();
        }
    }

    private static boolean isIsbn13Valid(String isbn) {
        if (isbn.length() != 13) return false;

        int lastDigit;
        char lastCharacter = isbn.charAt(
                isbn.length() - 1
        );

        if (Character.isDigit(lastCharacter)) {
            lastDigit = Character.getNumericValue(
                    isbn.charAt(isbn.length() - 1)
            );
        } else {
            return false;
        }

        int keyValue;
        int someOfTwelve = 0;
        for (int i = 0; i < isbn.length() - 1; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }

            int current = Character.getNumericValue(c);
            if (i % 2 != 0) {
                current = current * 3;
            }

            someOfTwelve += current;
        }

        keyValue = 10 - someOfTwelve % 10;
        return keyValue == lastDigit;
    }

    public static LastName randomLastName() {
        return new LastName(faker.name().lastName());
    }

    public static Password randomPassword() {
        return new Password(faker.internet().password(5, 48));
    }

    public static Phone randomPhone() {
        return new Phone(faker.phoneNumber().phoneNumberNational());
    }

    public static PublisherName randomPublisherName() {
        return new PublisherName(faker.book().publisher());
    }

    public static Title randomTitle() {
        return new Title(faker.book().title());
    }

    public static CreditCard randomCreditCard() {
        return new CreditCard(randomCreditCardNumber(), randomDate());
    }

    private static String randomCreditCardNumber() {
        String bin = "";
        int length = 16;
        int randomNumberLength = length - (bin.length() + 1);

        StringBuilder builder = new StringBuilder(bin);
        for (int i = 0; i < randomNumberLength; i++) {
            int digit = random.nextInt(10);
            builder.append(digit);
        }

        int checkDigit = getCheckDigit(builder.toString());
        builder.append(checkDigit);

        return builder.toString();
    }

    private static int getCheckDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {

            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }

        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
    }

    private static LocalDate randomDate() {
        long start = LocalDate.of(2021, 1, 1).toEpochDay();
        long end = LocalDate.of(2027, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(start, end);
        return LocalDate.ofEpochDay(randomDay);
    }
}