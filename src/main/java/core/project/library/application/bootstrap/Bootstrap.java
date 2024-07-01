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
    private static Map<Book, Integer> books;
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
    }

    @Override
    public final void run(String... args) {
        populatePublishers();
        populateAuthors();
        populateBooks();
        populateCustomers();
        populateOrders();

        if (bookRepository.count() < 1) {
            publishers.forEach(publisherRepository::savePublisher);
            authors.forEach(authorRepository::saveAuthor);
            books.keySet().forEach(bookRepository::completelySaveBook);
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
                .collect(Collectors.toMap(book -> book, _ -> faker.number().numberBetween(1, 20)));
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

            int randomPublisher = faker.number().numberBetween(0, MAX_NUMBER_OF_PUBLISHERS);
            Set<Author> authorsForBook = getAuthorsForBook();

            return Book.builder()
                    .id(UUID.randomUUID())
                    .title(randomTitle())
                    .description(randomDescription())
                    .isbn(randomISBN13())
                    .price(randomPrice())
                    .quantityOnHand(randomQuantityOnHand())
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
                    .paidAmount(new PaidAmount((double) faker.number().numberBetween(1, 5000)))
                    .creditCard(randomCreditCard())
                    .creationDate(LocalDateTime.now())
                    .customer(customers.get(randomCustomer))
                    .books(booksForOrder)
                    .build();
        };
    }

    private static Map<Book, Integer> getBooksForOrder(int countOfBooks) {
        List<Book> bookList = books.keySet().stream().toList();

        return Stream.generate(() -> {
            int randomBook = faker.number().numberBetween(0, bookList.size());

            Book book = bookList.get(randomBook);

            books.computeIfPresent(book, (_, count) -> count - 1);

            return book;
        }).filter(book -> books.get(book) > 0)
                .limit(countOfBooks)
                .collect(Collectors.toMap(book -> book, _ -> 1, Integer::sum));
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
                15, 255, true, true, true)
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
        List<String> phoneNumbers = List.of(
                "+994 36 554 02 38", "+994 60 316 52 73", "+994 10 317 38 31", "+994 10 289 43 41",
                        "+994 36 554 66 56", "+994 44 823 85 24", "+994 50 677 11 08", "+994 55 475 95 06",
                        "+994 50 404 64 77", "+994 60 879 58 61", "+994 36 554 10 27", "+994 60 579 64 80",
                        "+994 36 554 80 28", "+994 36 554 29 58", "+994 10 979 68 58", "+994 36 554 96 37",
                        "+994 44 065 24 17", "+994 36 554 63 01", "+994 60 359 37 51", "+994 36 554 24 01",
                        "+994 36 554 93 53", "+994 60 447 01 60", "+994 36 554 78 36", "+994 51 764 35 14",
                        "+994 10 286 01 43", "+994 36 554 43 51", "+994 36 554 55 41", "+994 10 679 56 46",
                        "+994 36 554 29 48", "+994 44 357 05 96", "+994 36 554 66 88", "+994 44 831 46 76",
                        "+994 36 554 44 40", "+994 36 554 62 41", "+994 36 554 81 53", "+994 60 429 53 21",
                        "+994 36 554 76 85", "+994 99 516 18 34", "+994 36 554 56 54", "+994 36 554 38 18",
                        "+994 10 521 37 80", "+994 10 373 72 11", "+994 36 554 99 22", "+994 36 554 37 84",
                        "+994 36 554 63 40", "+994 36 554 23 90", "+994 36 554 29 52", "+994 40 349 53 57",
                        "+994 36 554 85 89", "+994 70 980 14 94", "+994 36 554 94 13", "+994 36 554 88 92",
                        "+994 36 554 86 59", "+994 36 554 17 54", "+994 36 554 03 71", "+994 70 872 22 66",
                        "+994 40 324 41 16", "+994 36 554 93 89", "+994 77 373 48 11", "+994 40 916 20 80",
                        "+994 36 554 38 38", "+994 36 554 50 55", "+994 36 554 57 03", "+994 36 554 17 71",
                        "+994 36 554 37 08", "+994 36 554 18 53", "+994 36 554 00 71", "+994 36 554 83 46",
                        "+994 36 554 60 36", "+994 36 554 04 02", "+994 44 495 07 43", "+994 60 292 39 47",
                        "+994 36 554 39 84", "+994 36 554 20 79", "+994 36 554 88 52", "+994 36 554 48 98",
                        "+994 44 388 19 80", "+994 44 392 09 56", "+994 36 554 07 65", "+994 36 554 98 66",
                        "+994 40 855 94 94", "+994 36 554 21 03", "+994 60 890 72 56", "+994 36 554 65 49",
                        "+994 60 225 11 57", "+994 40 124 58 24", "+994 36 554 84 47", "+994 36 554 70 24",
                        "+994 36 554 30 04", "+994 10 652 07 38", "+994 36 554 53 72", "+994 40 632 87 29",
                        "+994 36 554 37 11", "+994 36 554 81 72", "+994 99 338 81 24", "+994 10 214 97 51",
                        "+994 36 554 90 64", "+994 36 554 16 81", "+994 36 554 40 82", "+994 36 554 69 39",
                        "+994 55 990 86 95", "+994 36 554 59 32", "+994 44 538 81 47", "+994 36 554 85 07",
                        "+994 99 320 15 25", "+994 44 201 60 24", "+994 36 554 39 28", "+994 36 554 96 18",
                        "+994 40 459 37 65", "+994 40 688 11 14", "+994 36 554 52 70", "+994 51 074 47 13",
                        "+994 10 428 89 19", "+994 60 529 78 90", "+994 36 554 33 06", "+994 10 085 83 62",
                        "+994 36 554 53 25", "+994 36 554 64 34", "+994 40 186 80 36", "+994 36 554 12 60",
                        "+994 10 631 16 51", "+994 36 554 50 17", "+994 36 554 46 80", "+994 36 554 34 69",
                        "+994 36 554 62 63", "+994 36 554 10 88", "+994 10 567 53 58", "+994 70 106 25 67",
                        "+994 36 554 20 26", "+994 50 900 30 46", "+994 36 554 65 70", "+994 44 345 37 54",
                        "+994 40 891 40 60", "+994 60 389 70 42", "+994 44 103 87 13", "+994 36 554 32 68",
                        "+994 36 554 99 84", "+994 70 186 64 59", "+994 36 554 95 97", "+994 77 629 84 03",
                        "+994 36 554 51 25", "+994 60 583 11 18", "+994 99 681 24 12", "+994 44 846 80 02",
                        "+994 36 554 03 62", "+994 36 554 47 86", "+994 10 753 54 33", "+994 60 391 35 45",
                        "+994 36 554 29 03", "+994 36 554 33 52", "+994 36 554 44 88", "+994 36 554 08 29",
                        "+994 36 554 80 35", "+994 36 554 07 20", "+994 40 139 78 88", "+994 10 887 79 17",
                        "+994 40 735 74 04", "+994 40 020 29 52", "+994 36 554 42 79", "+994 36 554 66 93",
                        "+994 36 554 47 34", "+994 10 816 82 59", "+994 36 554 56 55", "+994 10 727 11 06",
                        "+994 36 554 79 81", "+994 36 554 33 01", "+994 10 742 04 65", "+994 36 554 40 63",
                        "+994 44 283 68 66", "+994 77 092 55 60", "+994 60 297 44 41", "+994 99 393 91 81",
                        "+994 60 289 94 17", "+994 36 554 74 77", "+994 36 554 84 56", "+994 10 058 69 19",
                        "+994 36 554 48 01", "+994 44 444 28 82", "+994 36 554 80 38", "+994 36 554 67 32",
                        "+994 44 992 42 58", "+994 36 554 05 86", "+994 10 745 73 54", "+994 10 446 77 70",
                        "+994 10 955 23 43", "+994 36 554 03 16", "+994 10 636 42 04", "+994 36 554 99 24",
                        "+994 10 629 96 52", "+994 40 546 68 81", "+994 36 554 07 07", "+994 60 143 23 23",
                        "+994 36 554 32 34", "+994 40 169 74 76", "+994 51 860 98 81", "+994 10 221 91 20",
                        "+994 60 089 67 86", "+994 36 554 42 43", "+994 36 554 04 70", "+994 36 554 21 99",
                        "+994 36 554 29 72", "+994 36 554 96 85", "+994 10 848 15 11", "+994 36 554 66 00",
                        "+994 36 554 95 42", "+994 36 554 73 63", "+994 36 554 33 40", "+994 10 869 19 28",
                        "+994 36 554 09 75", "+994 51 277 83 43", "+994 99 414 36 15", "+994 36 554 72 98",
                        "+994 99 853 83 48", "+994 55 041 03 66", "+994 70 447 87 87", "+994 36 554 67 37",
                        "+994 36 554 46 58", "+994 10 836 11 76", "+994 40 570 86 57", "+994 36 554 60 78",
                        "+994 60 946 03 44", "+994 40 430 29 37", "+994 36 554 39 91", "+994 36 554 25 94",
                        "+994 44 112 32 48", "+994 40 666 10 83", "+994 36 554 26 63", "+994 36 554 28 84",
                        "+994 60 037 04 78", "+994 36 554 65 99", "+994 60 705 72 03", "+994 60 020 75 99",
                        "+994 60 185 94 09", "+994 40 200 67 53", "+994 36 554 59 76", "+994 36 554 02 71",
                        "+994 10 603 28 57", "+994 36 554 64 16", "+994 36 554 74 06", "+994 36 554 38 58",
                        "+994 44 199 12 57", "+994 10 630 30 69", "+994 60 934 53 65", "+994 36 554 05 78",
                        "+994 36 554 44 21", "+994 40 328 58 58", "+994 99 128 65 35", "+994 36 554 47 00",
                        "+994 10 802 09 37", "+994 99 748 55 55", "+994 40 696 95 55", "+994 60 844 03 21",
                        "+994 60 298 06 95", "+994 36 554 01 18", "+994 10 273 31 13", "+994 36 554 29 80",
                        "+994 36 554 87 35", "+994 36 554 90 32", "+994 36 554 12 20", "+994 36 554 32 92",
                        "+994 36 554 42 19", "+994 50 668 38 38", "+994 36 554 01 76", "+994 36 554 75 94",
                        "+994 36 554 71 28", "+994 99 451 43 18", "+994 40 112 15 19", "+994 10 040 79 03",
                        "+994 36 554 11 87", "+994 44 557 27 68", "+994 10 786 53 20", "+994 70 563 09 97",
                        "+994 36 554 68 41", "+994 36 554 64 55", "+994 36 554 93 25", "+994 36 554 11 65",
                        "+994 44 772 54 52", "+994 50 391 63 05", "+994 36 554 44 29", "+994 60 352 74 49",
                        "+994 55 609 81 74", "+994 77 057 26 25", "+994 60 965 44 11", "+994 10 465 13 98",
                        "+994 51 518 63 34", "+994 51 973 92 82", "+994 36 554 94 46", "+994 36 554 99 16",
                        "+994 36 554 98 06", "+994 36 554 87 34", "+994 36 554 82 23", "+994 36 554 48 86",
                        "+994 44 003 08 70", "+994 36 554 59 74", "+994 60 790 94 46", "+994 60 024 53 76",
                        "+994 36 554 72 48", "+994 36 554 15 37", "+994 50 661 69 57", "+994 55 246 06 77"
        );


        String randomPhone = phoneNumbers.get(faker.number().numberBetween(0, phoneNumbers.size() - 1));

        return new Phone(randomPhone);
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

    public static String randomCreditCardNumber() {
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

    public static LocalDate randomDate() {
        long start = LocalDate.of(2021, 1, 1).toEpochDay();
        long end = LocalDate.of(2027, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(start, end);
        return LocalDate.ofEpochDay(randomDay);
    }

    public static Price randomPrice() {
        return new Price(faker.number().randomDouble(2, 1, 100));
    }

    public static QuantityOnHand randomQuantityOnHand() {
        return new QuantityOnHand(faker.number().numberBetween(1, 15));
    }
}