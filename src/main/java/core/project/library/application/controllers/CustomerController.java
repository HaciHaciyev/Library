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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller managing customer-related operations in the library system.
 *
 * <p>This controller provides endpoints for performing CRUD (Create, Read, Update, Delete)
 * operations on customers, as well as adding books to their accounts.</p>
 *
 * <p>Each endpoint interacts with the {@link CustomerService} to execute the corresponding
 * business logic.</p>
 *
 * <p>The controller utilizes an {@link EntityMapper} to facilitate conversion between
 * entity and model objects. This abstraction helps maintain separation of concerns and
 * promotes code reuse by centralizing the conversion logic.</p>
 */
@Slf4j
@RestController
@RequestMapping("/library/customer")
public class CustomerController {

    private final EntityMapper entityMapper;

    private final CustomerService customerService;

    /**
     * Constructs a new {@code CustomerController} with the given {@code EntityMapper} and {@code CustomerService}.
     *
     * @param entityMapper the entity mapper
     * @param customerService the customer service
     */
    public CustomerController(EntityMapper entityMapper, CustomerService customerService) {
        this.entityMapper = entityMapper;
        this.customerService = customerService;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId the ID of the customer
     * @return a {@code ResponseEntity} containing the {@code CustomerModel} of the found customer
     * @throws NotFoundException if the customer is not found
     */
    @GetMapping("/getCustomerById/{customerId}")
    public ResponseEntity<CustomerModel> getCustomerById(@PathVariable UUID customerId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new)));
    }

    /**
     * Adds books to a customer's account.
     *
     * @param customerId the ID of the customer
     * @param bookUUIDs the list of book UUIDs to add to the customer's account
     * @return a {@code ResponseEntity} containing the updated {@code CustomerModel}
     * @throws NotFoundException if the customer or books are not found
     */
    @PostMapping("/addBookToCustomer/{customerId}")
    public ResponseEntity<CustomerModel> addBookToCustomerAccount(@PathVariable UUID customerId,
                                                                  @RequestBody List<UUID> bookUUIDs) {
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        customerService.addBookToCustomer(customerId, bookUUIDs).orElseThrow(NotFoundException::new)
                ));
    }

    /**
     * Saves a new customer.
     *
     * @param model the customer model to save
     * @return a {@code ResponseEntity} with the location of the saved customer
     * @throws NotFoundException if the customer could not be saved
     */
    @PostMapping("/saveCustomer")
    public ResponseEntity<Void> saveCustomer(@RequestBody @Valid CustomerModel model) {
        Customer customer = Customer.from(model);
        Optional<Customer> savedCustomer = customerService.saveCustomer(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/library/customer/getCustomerById/"
                + savedCustomer.orElseThrow(NotFoundException::new)
                .getId().toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Updates an existing customer.
     *
     * @param model the customer model to update
     * @return a {@code ResponseEntity} with the location of the updated customer
     * @throws NotFoundException if the customer could not be updated
     */
    @PutMapping("/updateCustomer")
    public ResponseEntity<Void> updateCustomer(@RequestBody @Valid CustomerModel model) {
        Customer customer = Customer.from(model);
        Optional<Customer> updatedCustomer = customerService.updateCustomer(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/library/customer/updateCustomer"
                + updatedCustomer.orElseThrow(NotFoundException::new)
                .getId().toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
