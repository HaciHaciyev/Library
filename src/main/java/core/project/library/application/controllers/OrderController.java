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

/**
 * The OrderController class is responsible for managing HTTP requests related to orders within the library application.
 * It serves as the entry point for handling various operations such as retrieving order details by ID.
 * <p>
 * The OrderController class works in conjunction with the {@link OrderService} and {@link EntityMapper} components.
 * <p>
 * This controller exposes the following endpoint:
 * <ul>
 * <li><strong>GET /library/order/getOrderById/{orderId}</strong> - Retrieves detailed information about an order
 * identified by its unique ID.</li>
 * </ul>
 * The controller returns responses in JSON format.
 * In case an order is not found, a {@link NotFoundException} is thrown, resulting in a 404 error response.
 */
@RestController
@RequestMapping("/library/order")
public class OrderController {

    private final OrderService orderService;

    private final EntityMapper entityMapper;

    /**
     * Constructs an instance of OrderController.
     *
     * @param orderService The OrderService responsible for handling order-related operations.
     * @param entityMapper The EntityMapper responsible for mapping entities to models.
     */
    public OrderController(OrderService orderService, EntityMapper entityMapper) {
        this.orderService = orderService;
        this.entityMapper = entityMapper;
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId The unique identifier of the order to retrieve.
     * @return A ResponseEntity containing the order details if found, or a 404 error if not found.
     */
    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderModel> getOrderById(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        orderService.getOrderById(orderId).orElseThrow(NotFoundException::new))
                );
    }
}
