package core.project.library.infrastructure.service;

import core.project.library.domain.entities.Customer;
import core.project.library.infrastructure.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Optional<Customer> findById(UUID customerId) {
        return repository.findById(customerId);
    }

    public Optional<List<Customer>> findByLastName(String lastName) {
        return repository.findByLastName(lastName);
    }
}
