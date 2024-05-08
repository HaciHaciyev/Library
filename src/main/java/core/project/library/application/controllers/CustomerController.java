package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
