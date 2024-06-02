package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerDTO;
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

    private final EntityMapper mapper;

    private final CustomerRepository customerRepository;

    public CustomerController(EntityMapper entityMapper, CustomerRepository customerRepository) {
        this.mapper = entityMapper;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/findById/{id}")
    ResponseEntity<CustomerDTO> findById(@PathVariable("id") UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDTO(customerRepository
                        .findById(id).orElseThrow(NotFoundException::new)));
    }

    @GetMapping("/findByLastName/{lastName}")
    ResponseEntity<List<CustomerDTO>> findByLastName(@PathVariable("lastName") String lastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerRepository
                        .findByLastName(lastName)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(mapper::toDTO).toList());
    }
}
