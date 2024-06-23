package core.project.library.application.controllers;

import core.project.library.application.model.CustomerDTO;
import core.project.library.domain.entities.Customer;
import core.project.library.infrastructure.mappers.CustomerMapper;
import core.project.library.infrastructure.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/library/customer")
public class CustomerController {

    private final CustomerMapper customerMapper;

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerMapper customerMapper, CustomerRepository customerRepository) {
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/findById/{customerId}")
    final ResponseEntity<CustomerDTO> findById(@PathVariable("customerId") UUID customerId) {
        var customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        return ResponseEntity.ok(customerMapper.toDTO(customer));
    }

    @GetMapping("/findByLastName/{customerLastName}")
    final ResponseEntity<List<CustomerDTO>> findByLastName(@PathVariable("customerLastName") String customerLastName) {
        var customers = customerRepository
                .findByLastName(customerLastName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        return ResponseEntity.ok(customerMapper.listOfDTO(customers));
    }

    @PostMapping("/saveCustomer")
    final ResponseEntity<String> saveCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
        Customer customer = customerMapper.customerFromDTO(customerDTO);

        var customerResult = customerRepository.saveCustomer(customer);

        customerResult.ifFailure(this::throwIfFailure);

        Customer savedCustomer = customerResult.value();

        return ResponseEntity
                .created(URI.create("/library/customer/findById/" + savedCustomer.getId()))
                .body("Successfully saved customer");
    }

    private void throwIfFailure(Exception e) {
        if (e instanceof IllegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save customer");
        }
    }
}
