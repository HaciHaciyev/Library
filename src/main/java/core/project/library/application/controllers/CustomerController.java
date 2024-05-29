package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.service.CustomerService;
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
    private final CustomerService service;

    public CustomerController(EntityMapper entityMapper, CustomerService service) {
        this.mapper = entityMapper;
        this.service = service;
    }

    @GetMapping("/findById/{id}")
    ResponseEntity<CustomerDTO> findById(@PathVariable("id") UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDTO(service
                        .findById(id).orElseThrow(NotFoundException::new)));
    }

    @GetMapping("/findByLastName/{lastName}")
    ResponseEntity<List<CustomerDTO>> findByLastName(@PathVariable("lastName") String lastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service
                        .findByLastName(lastName)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(mapper::toDTO).toList());
    }
}
