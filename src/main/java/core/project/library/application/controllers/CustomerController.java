package core.project.library.application.controllers;

import core.project.library.application.mappers.CustomerMapper;
import core.project.library.application.model.CustomerDTO;
import core.project.library.application.model.CustomerModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/customer")
public class CustomerController {

    private final CustomerMapper mapper;

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerMapper mapper, CustomerRepository customerRepository) {
        this.mapper = mapper;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/findById/{customerId}")
    ResponseEntity<CustomerDTO> findById(@PathVariable("customerId") UUID customerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.dtoFrom(customerRepository
                        .findById(customerId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByLastName/{customerLastName}")
    ResponseEntity<List<CustomerDTO>> findByLastName(@PathVariable("customerLastName") String customerLastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.dtosFrom(customerRepository
                        .findByLastName(customerLastName)
                        .orElseThrow(NotFoundException::new)));
    }
}
