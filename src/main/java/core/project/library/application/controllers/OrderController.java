package core.project.library.application.controllers;

import core.project.library.application.mappers.OrderMapper;
import core.project.library.application.model.OrderModel;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.CustomerRepository;
import core.project.library.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/library/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMapper mapper;

    private final BookRepository bookRepository;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    @GetMapping("/findById/{orderId}")
    final ResponseEntity<OrderModel> findById(@PathVariable("orderId")UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toModel(
                        orderRepository.findById(orderId).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/findByCustomerId/{customerIdForOrders}")
    final ResponseEntity<List<OrderModel>> findByCustomerId(@PathVariable("customerIdForOrders")UUID customerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.listOfModel(orderRepository
                        .findByCustomerId(customerId)));
    }

    @GetMapping("/findByBookId/{bookIdForOrders}")
    final ResponseEntity<List<OrderModel>> findByBookId(@PathVariable("bookIdForOrders")UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderRepository
                        .findByBookId(bookId)
                        .stream().map(mapper::toModel).toList());
    }

    @PostMapping("/createOrder")
    final ResponseEntity<Void> createOrder(@RequestParam UUID customerId,
                                           @RequestParam List<UUID> booksId) {
        if (!customerRepository.isCustomerExists(customerId)) {
            throw new IllegalArgumentException("Customer was not found");
        }

        for (UUID bookId : booksId) {
            if (!bookRepository.isBookExists(bookId)) {
                throw new IllegalArgumentException("Book was not found");
            }
        }

        Customer customer = customerRepository.findById(customerId).get();

        double totalPrice = 0.0;
        Set<Book> books = new HashSet<>();
        for (UUID bookId : booksId) {
            Book book = bookRepository.findById(bookId).get();
            books.add(book);
            totalPrice += book.getPrice().doubleValue();
        }

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(books.size())
                .totalPrice(new TotalPrice(BigDecimal.valueOf(totalPrice)))
                .events(new Events())
                .customer(customer)
                .books(books)
                .build();

        orderRepository.saveOrder(order);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", String.format("/library/order/findById/%s", order.getId()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
