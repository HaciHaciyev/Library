package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final EntityMapper mapper;

    private final CustomerRepository customerRepository;

    public CustomerController(EntityMapper entityMapper, CustomerRepository customerRepository) {
        this.mapper = entityMapper;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/findById/{customerId}")
    ResponseEntity<CustomerDTO> findById(@PathVariable("customerId") UUID customerId) {
        log.info("Controller was`t found");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDTO(customerRepository
                        .findById(customerId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByLastName/{customerLastName}")
    ResponseEntity<List<CustomerDTO>> findByLastName(@PathVariable("customerLastName") String customerLastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerRepository
                        .findByLastName(customerLastName)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(mapper::toDTO).toList());
    }
}
