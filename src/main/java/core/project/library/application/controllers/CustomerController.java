package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerModel;
import core.project.library.domain.entities.Customer;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/library/customer")
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

    @PostMapping("/saveCustomer")
    public ResponseEntity saveCustomer(@RequestBody @Valid CustomerModel model) {
        Customer customer = Customer.from(model);
        Optional<Customer> savedCustomer = customerService.saveCustomer(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/library/customer/getCustomerById/"
                + savedCustomer.orElseThrow().getId().toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/updateCustomer")
    public ResponseEntity updateCustomer(@RequestBody @Valid CustomerModel model) {
        Customer customer = Customer.from(model);
        Optional<Customer> updatedCustomer = customerService.updateCustomer(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/library/customer/updateCustomer"
                + updatedCustomer.get().getId().toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
