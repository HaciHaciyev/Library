package core.project.library.infrastructure.services;

import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.CustomerRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public Optional<Customer> getCustomerById(UUID customerId) {
        return entityCollectorForCustomer(
                customerRepository.getCustomerById(customerId).orElseThrow(),
                orderRepository.getOrdersByCustomerId(customerId)
        );
    }

    private Optional<Customer> entityCollectorForCustomer(Customer customer, List<Optional<Order>> orders) {
        Set<Order> orderSet = new HashSet<>();
        orders.forEach(order -> orderSet.add(order.orElseThrow(NotFoundException::new)));

        return Optional.ofNullable(Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .password(customer.getPassword())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .events(customer.getEvents())
                .orders(orderSet)
                .build());
    }
}
