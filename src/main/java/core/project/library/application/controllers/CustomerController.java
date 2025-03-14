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
        var customers = customerRepository.findByLastName(customerLastName);

        if (customers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        return ResponseEntity.ok(customerMapper.listOfDTO(customers));
    }

    @PostMapping("/saveCustomer")
    final ResponseEntity<String> saveCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
        if (customerRepository.emailExists(customerDTO.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Customer customer = customerMapper.customerFromDTO(customerDTO);

        var savedCustomer = customerRepository.saveCustomer(customer)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save customer"));

        return ResponseEntity
                .created(URI.create("/library/customer/findById/" + savedCustomer.getId()))
                .body("Successfully saved customer");
    }
}
