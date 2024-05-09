package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/customer/")
public class CustomerController {

    private final EntityMapper entityMapper;

    private final CustomerService customerService;

    public CustomerController(EntityMapper entityMapper, CustomerService customerService) {
        this.entityMapper = entityMapper;
        this.customerService = customerService;
    }

    @GetMapping("/getCustomerById/{customerId}")
    public ResponseEntity<CustomerModel> getCustomerById(@PathVariable UUID customerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new)));
    }

    @PostMapping("/addBookToCustomer/{customerId}")
    public ResponseEntity<CustomerModel> addBookToCustomerAccount(@PathVariable UUID customerId,
                                                                  @RequestBody List<UUID> book_uuids) {
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        customerService.addBookToCustomer(customerId, book_uuids).orElseThrow(NotFoundException::new)
                ));
    }
}
