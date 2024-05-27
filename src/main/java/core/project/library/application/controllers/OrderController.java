package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.OrderModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.service.OrderService;
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

    private final EntityMapper entityMapper;

    private final OrderService orderService;

    public OrderController(EntityMapper entityMapper, OrderService orderService) {
        this.entityMapper = entityMapper;
        this.orderService = orderService;
    }

    @GetMapping("/findById/{orderId}")
    final ResponseEntity<OrderModel> findById(@PathVariable("orderId")UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        orderService.findById(orderId).orElseThrow(NotFoundException::new)
                ));
    }
}
