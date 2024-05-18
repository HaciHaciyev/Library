package core.project.library.infrastructure.services;

import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.BookRepository;
import core.project.library.infrastructure.repositories.CustomerRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final BookRepository bookRepository;

    public Optional<Order> getOrderById(UUID orderId) {
        Order order = orderRepository.getOrderById(orderId).orElseThrow(NotFoundException::new);
        Customer customer = customerRepository.getCustomerById(
                orderRepository.getCustomerId(orderId)
        ).orElseThrow(NotFoundException::new);
        List<Book> bookList = bookRepository.getBooksByOrderId(orderId);

        return entityCollectorForOrder(order, customer, bookList);
    }

    private static Optional<Order> entityCollectorForOrder(Order order, Customer customer, List<Book> bookList) {
        Order resultOrder = Order.builder()
                .id(order.getId())
                .countOfBooks(order.getCountOfBooks())
                .totalPrice(order.getTotalPrice())
                .events(order.getEvents())
                .build();

        customer.addOrder(resultOrder);
        Set<Book> books = new HashSet<>(bookList);
        books.forEach(book -> book.addOrder(resultOrder));

        return Optional.ofNullable(resultOrder);
    }
}
