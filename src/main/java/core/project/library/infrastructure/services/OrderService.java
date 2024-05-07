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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final BookRepository bookRepository;

    public Optional<Order> getOrderById(UUID orderId) {
        return entityCollectorForOrder(
                orderRepository.getOrderById(orderId).orElseThrow(NotFoundException::new),
                customerRepository.getCustomerByOrderId(orderId).orElseThrow(NotFoundException::new),
                bookRepository.getBooksByOrderId(orderId)
        );
    }

    private static Optional<Order> entityCollectorForOrder(Order order, Customer customer, List<Book> bookList) {
        return Optional.ofNullable(
                Order.builder()
                        .id(order.getId())
                        .countOfBooks(order.getCountOfBooks())
                        .totalPrice(order.getTotalPrice())
                        .events(order.getEvents())
                        .customer(customer)
                        .books(new HashSet<>(bookList))
                        .build()
        );
    }
}
