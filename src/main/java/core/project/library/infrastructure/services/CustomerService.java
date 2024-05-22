package core.project.library.infrastructure.services;

import core.project.library.domain.entities.*;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.data_transfer.BookDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    public Optional<Customer> getCustomerById(UUID customerId) {
        return entityCollectorForCustomer(
                customerRepository.getCustomerById(customerId).orElseThrow(),
                orderRepository.getOrdersByCustomerId(customerId)
        );
    }

    public Optional<Customer> saveCustomer(Customer customer) {
        return customerRepository.saveCustomer(customer);
    }

    public Optional<Customer> addBookToCustomer(UUID customerId, List<UUID> bookUUIDs) {
        /** Get exists Customer and validate to existing in database.*/
        Optional<Customer> optionalCustomer = getCustomerById(customerId);
        if (optionalCustomer.isEmpty()) return Optional.empty();

        /** Count of total price of order and prepare books for order.*/
        long totalPrice = 0;
        Set<Book> booksForOrder = new HashSet<>();
        for (UUID bookId : bookUUIDs) {
            Optional<BookDTO> optionalBook =
                    bookRepository.getBookById(bookId);

            if (optionalBook.isEmpty()) {
                return Optional.empty();
            } else {
                totalPrice = totalPrice + optionalBook.get().price().longValue();

                Optional<UUID> publisherId = bookRepository.getPublisherId(bookId);
                if (publisherId.isEmpty()) {
                    return Optional.empty();
                }
                List<Author> authors = authorRepository.getAuthorsByBookId(bookId);
                Publisher publisher = publisherRepository.getPublisherById(publisherId.get()).orElseThrow(NotFoundException::new);
                booksForOrder.add(entityCollectorForBook(optionalBook.get(), publisher, authors));
            }
        }

        /** Creation of Order*/
        Order newOrder = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(booksForOrder.size())
                .totalPrice(new TotalPrice(BigDecimal.valueOf(totalPrice)))
                .events(new Events())
                .customer(optionalCustomer.get())
                .books(booksForOrder)
                .build();

        /** Save new Order*/
        Optional<Order> savedOrder = orderRepository.saveOrder(newOrder);
        /**Save IDs for Book_Order join table*/
        booksForOrder.forEach(book -> bookRepository.saveBookOrder(book, savedOrder.orElseThrow()));

        return getCustomerById(customerId);
    }

    public Optional<Customer> updateCustomer(Customer customer) {
        return customerRepository.updateCustomer(customer);
    }

    private Optional<Customer> entityCollectorForCustomer(Customer customer, List<Order> orders) {
        Customer resultCustomer = Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .password(customer.getPassword())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .events(customer.getEvents())
                .build();

        orders.forEach(order -> Order.builder()
                .id(order.getId())
                .countOfBooks(order.getCountOfBooks())
                .totalPrice(order.getTotalPrice())
                .events(order.getEvents())
                .customer(resultCustomer)
                .books(order.getBooks())
                .build());

        return Optional.of(resultCustomer);
    }

    private Book entityCollectorForBook(
            BookDTO bookDTO, Publisher publisher, List<Author> authors) {
        return Book.builder()
                .id(bookDTO.id())
                .title(bookDTO.title())
                .description(bookDTO.description())
                .isbn(bookDTO.isbn())
                .price(bookDTO.price())
                .quantityOnHand(bookDTO.quantityOnHand())
                .category(bookDTO.category())
                .events(bookDTO.events())
                .category(bookDTO.category())
                .publisher(publisher)
                .authors(new HashSet<>(authors))
                .build();
    }
}
