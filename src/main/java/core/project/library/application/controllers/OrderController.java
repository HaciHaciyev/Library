package core.project.library.application.controllers;

import core.project.library.application.model.OrderModel;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.mappers.OrderMapper;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.CustomerRepository;
import core.project.library.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

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
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return ResponseEntity.ok(mapper.toModel(order));
    }

    @GetMapping("/findByCustomerId/{customerIdForOrders}")
    final ResponseEntity<List<OrderModel>> findByCustomerId(@PathVariable("customerIdForOrders")UUID customerId) {
        var orders = orderRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer either made no orders or he does not exist"));

        return ResponseEntity.ok(mapper.listOfModel(orders));
    }

    @GetMapping("/findByBookId/{bookIdForOrders}")
    final ResponseEntity<List<OrderModel>> findByBookId(@PathVariable("bookIdForOrders")UUID bookId) {
        var orders = orderRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return ResponseEntity.ok(mapper.listOfModel(orders));
    }

    @PostMapping("/createOrder")
    final ResponseEntity<String> createOrder(@RequestParam UUID customerId,
                                             @RequestParam List<UUID> booksId) {

        Order order = mapOrder(customerId, booksId);

        var savedOrder = orderRepository.save(order)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not save order"));

        return ResponseEntity
                .created(URI.create("/library/order/findById/" + savedOrder.getId()))
                .body("Succesfully created order");
    }

    private Order mapOrder(UUID customerId, List<UUID> booksId) {

        Map<Book, Integer> books = booksId.stream()
                                          .map(this::getBook)
                                          .collect(Collectors.toMap(book -> book, _ -> 1, Integer::sum));

        Integer countOfBooks = books.values()
                                    .stream()
                                    .reduce(0, Integer::sum);

        BigDecimal totalPrice = books.keySet()
                                     .stream()
                                     .map(Book::getPrice)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid customer ID"));

        return Order.builder()
                .id(UUID.randomUUID())
                .countOfBooks(countOfBooks)
                .totalPrice(new TotalPrice(totalPrice))
                .events(new Events())
                .customer(customer)
                .books(books)
                .build();
    }

    private Book getBook(UUID uuid) {
        return bookRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid book ID -> " + uuid));
    }
}
