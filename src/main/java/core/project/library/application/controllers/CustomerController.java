package core.project.library.application.controllers;

import core.project.library.application.model.CustomerDTO;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.mappers.CustomerMapper;
import core.project.library.infrastructure.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerMapper.toDTO(customerRepository
                        .findById(customerId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByLastName/{customerLastName}")
    final ResponseEntity<List<CustomerDTO>> findByLastName(@PathVariable("customerLastName") String customerLastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerMapper.listOfDTO(customerRepository
                        .findByLastName(customerLastName)
                        .orElseThrow(NotFoundException::new)));
    }

    @PostMapping("/saveCustomer")
    final ResponseEntity<Void> saveCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
        if (customerRepository.isEmailExists(customerDTO.email())) {
            throw new IllegalArgumentException("Email was be used");
        }

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName(customerDTO.firstName())
                .lastName(customerDTO.lastName())
                .password(customerDTO.password())
                .email(customerDTO.email())
                .address(customerDTO.address())
                .events(new Events())
                .build();

        customerRepository.saveCustomer(customer);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", String.format("/library/customer/saveCustomer/%s", customer.getId().toString()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
