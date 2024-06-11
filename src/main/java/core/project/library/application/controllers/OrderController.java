package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.OrderModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/order")
public class OrderController {

    private final EntityMapper entityMapper;

    private final OrderRepository orderRepository;

    public OrderController(EntityMapper entityMapper, OrderRepository orderRepository) {
        this.entityMapper = entityMapper;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/findById/{orderId}")
    final ResponseEntity<OrderModel> findById(@PathVariable("orderId")UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        orderRepository.findById(orderId).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/findByCustomerId/{customerIdForOrders}")
    final ResponseEntity<List<OrderModel>> findByCustomerId(@PathVariable("customerIdForOrders")UUID customerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderRepository
                        .findByCustomerId(customerId)
                        .stream().map(entityMapper::toModel).toList()
                );
    }

    @GetMapping("/findByBookId/{bookIdForOrders}")
    final ResponseEntity<List<OrderModel>> findByBookId(@PathVariable("bookIdForOrders")UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderRepository
                        .findByCustomerId(bookId)
                        .stream().map(entityMapper::toModel).toList()
                );
    }
}
