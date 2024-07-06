package core.project.library.application.controllers;

import core.project.library.application.model.InboundOrderDTO;
import core.project.library.application.model.OrderModel;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.QuantityOnHandException;
import core.project.library.infrastructure.exceptions.RemovedFromSaleException;
import core.project.library.infrastructure.mappers.OrderMapper;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.CustomerRepository;
import core.project.library.infrastructure.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    final ResponseEntity<String> createOrder(@RequestBody @Valid InboundOrderDTO inboundOrderDTO) {

        Customer customer = customerRepository.findById(inboundOrderDTO.customerId()).orElseThrow(NotFoundException::new);

        Map<Book, Integer> books = inboundOrderDTO.booksId().stream()
                .map(bookId -> bookRepository.findById(bookId).orElseThrow(NotFoundException::new))
                .collect(Collectors.toMap(book -> book, _ -> 1, Integer::sum));

        for (Map.Entry<Book, Integer> pair : books.entrySet()) {
            Book book = pair.getKey();
            if (!book.isItOnSale()) throw new RemovedFromSaleException("Book is not on sale");

            int requiredQuantityForOneCopyOfBook = pair.getValue();
            int existedQuantityOnHand = book.getQuantityOnHand().quantityOnHand();

            boolean isQuantityOnHandEnough = existedQuantityOnHand >= requiredQuantityForOneCopyOfBook;
            if (!isQuantityOnHandEnough) {
                throw new QuantityOnHandException("We do not have enough books for this order.");
            }
        }

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .paidAmount(inboundOrderDTO.paidAmount())
                .creditCard(inboundOrderDTO.creditCard())
                .creationDate(LocalDateTime.now())
                .customer(customer)
                .books(books)
                .build();

        for (Map.Entry<Book, Integer> pair : books.entrySet()) {
            Book book = pair.getKey();
            int requiredQuantityForOneCopyOfBook = pair.getValue();
            int existedQuantityOnHand = book.getQuantityOnHand().quantityOnHand();

            book.changeQuantityOnHand(existedQuantityOnHand - requiredQuantityForOneCopyOfBook);
        }

        var savedOrder = orderRepository.save(order, books.keySet())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not save order"));

        return ResponseEntity
                .created(URI.create("/library/order/findById/" + savedOrder.getId()))
                .body("Successfully created order");
    }
}
