package core.project.library.infrastructure.service;

import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> findById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}
