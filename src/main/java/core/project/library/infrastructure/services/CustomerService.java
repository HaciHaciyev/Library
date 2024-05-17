package core.project.library.infrastructure.services;

import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.BookRepository;
import core.project.library.infrastructure.repositories.CustomerRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
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
            Optional<Book> optionalBook = bookRepository.getBookById(bookId);
            if (optionalBook.isEmpty()) {
                return Optional.empty();
            } else {
                totalPrice = totalPrice + optionalBook.get()
                        .getPrice().longValue();
                booksForOrder.add(optionalBook.get());
            }
        }

        /** Creation of Order*/
        Order newOrder = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(booksForOrder.size())
                .totalPrice(new TotalPrice(BigDecimal.valueOf(totalPrice)))
                .events(new Events())
                .build();
        optionalCustomer.get().addOrder(newOrder);
        booksForOrder.forEach(book -> book.addOrder(newOrder));

        /** Save new Order*/
        Optional<Order> savedOrder = orderRepository.saveOrder(newOrder);
        /**Save IDs for Book_Order join table*/
        booksForOrder.forEach(book -> bookRepository.saveBookOrder(book, savedOrder.orElseThrow()));
        /**Save IDs for Customer_Order join table*/
        customerRepository.saveCustomerOrder(optionalCustomer.get(), savedOrder.orElseThrow());

        return getCustomerById(customerId);
    }

    public Optional<Customer> updateCustomer(Customer customer) {
        return customerRepository.updateCustomer(customer);
    }

    private Optional<Customer> entityCollectorForCustomer(Customer customer, List<Optional<Order>> orders) {
        Set<Order> orderSet = new HashSet<>();
        orders.forEach(order -> orderSet.add(order.orElseThrow(NotFoundException::new)));

        Customer resultCustomer = Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .password(customer.getPassword())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .events(customer.getEvents())
                .build();

        orderSet.forEach(resultCustomer::addOrder);

        return Optional.of(resultCustomer);
    }
}
