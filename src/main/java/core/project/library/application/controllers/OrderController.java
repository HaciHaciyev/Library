package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.OrderModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/library/order")
public class OrderController {

    private final OrderService orderService;

    private final EntityMapper entityMapper;

    public OrderController(OrderService orderService, EntityMapper entityMapper) {
        this.orderService = orderService;
        this.entityMapper = entityMapper;
    }

    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderModel> getOrderById(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        orderService.getOrderById(orderId).orElseThrow(NotFoundException::new))
                );
    }
}
