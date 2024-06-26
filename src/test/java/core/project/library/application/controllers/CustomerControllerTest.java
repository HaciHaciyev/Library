package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.application.model.CustomerDTO;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.exceptions.Result;
import core.project.library.infrastructure.mappers.CustomerMapper;
import core.project.library.infrastructure.repository.CustomerRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final Faker faker = new Faker();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerRepository mockRepo;

    @MockBean
    CustomerMapper mockMapper;

    @Nested
    @DisplayName("FindById endpoint")
    class FindByIdTests {

        private static final String FIND_BY_ID = "/library/customer/findById/";

        private static Stream<Arguments> dtoEntity() {
            Customer customer = Bootstrap.customerFactory().get();

            CustomerDTO dto = new CustomerDTO(
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPassword(),
                    customer.getEmail(),
                    customer.getAddress()
            );

            return Stream.generate(() -> arguments(customer, dto))
                    .limit(1);
        }

        @ParameterizedTest
        @MethodSource("dtoEntity")
        @DisplayName("Accept UUID of existing customer")
        void testExisting(Customer customer, CustomerDTO customerDTO) throws Exception {
            when(mockRepo.findById(customer.getId())).thenReturn(Result.success(customer));
            when(mockMapper.toDTO(customer)).thenReturn(customerDTO);

            mockMvc.perform(get(FIND_BY_ID + customer.getId().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.firstName.firstName", is(customer.getFirstName().firstName())),
                            jsonPath("$.lastName.lastName", is(customer.getLastName().lastName())),
                            jsonPath("$.password.password", is(customer.getPassword().password())),
                            jsonPath("$.email.email", is(customer.getEmail().email())),
                            jsonPath("$.address.state", is(customer.getAddress().state())),
                            jsonPath("$.address.city", is(customer.getAddress().city())),
                            jsonPath("$.address.street", is(customer.getAddress().street())),
                            jsonPath("$.address.home", is(customer.getAddress().home()))
                    );
        }

        @Test
        @DisplayName("Reject UUID of non existent customer")
        void testNonExistent() throws Exception {
            when(mockRepo.findById(any(UUID.class))).thenReturn(Result.failure(null));

            mockMvc.perform(get(FIND_BY_ID + UUID.randomUUID())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Customer not found");
                    });
        }
    }

    @Nested
    @DisplayName("FindByLastName endpoint")
    class FindByLastNameTests {

        private static final String FIND_BY_LAST_NAME = "/library/customer/findByLastName/";

        private static Stream<Arguments> customersDtosName() {
            String lastName = faker.name().lastName();

            Supplier<Customer> customerSupplier = () -> Customer.builder()
                    .id(UUID.randomUUID())
                    .firstName(Bootstrap.randomFirstName())
                    .lastName(new LastName(lastName))
                    .password(Bootstrap.randomPassword())
                    .email(Bootstrap.randomEmail())
                    .address(Bootstrap.randomAddress())
                    .events(new Events())
                    .build();

            List<Customer> customers = Stream.generate(customerSupplier)
                    .limit(5)
                    .toList();

            List<CustomerDTO> dtos = customers.stream()
                    .map(customer -> new CustomerDTO(customer.getFirstName(),
                            customer.getLastName(),
                            customer.getPassword(),
                            customer.getEmail(),
                            customer.getAddress()))
                    .toList();

            return Stream.generate(() -> arguments(customers, dtos, lastName))
                    .limit(5);
        }

        @ParameterizedTest
        @MethodSource("customersDtosName")
        @DisplayName("Return customers with matching last name")
        void testWithMatchingName(List<Customer> customerList, List<CustomerDTO> dtos, String lastName) throws Exception {
            when(mockRepo.findByLastName(lastName)).thenReturn(Result.success(customerList));
            when(mockMapper.listOfDTO(customerList)).thenReturn(dtos);

            mockMvc.perform(get(FIND_BY_LAST_NAME + lastName)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.[*].lastName.lastName", everyItem(is(lastName)))
                    );
        }

        @Test
        @DisplayName("Throw exception in case of no match")
        void testNoMatch() throws Exception {
            String lastName = "lastName";
            when(mockRepo.findByLastName(lastName)).thenReturn(Result.failure(null));

            mockMvc.perform(get(FIND_BY_LAST_NAME + lastName)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Customer not found");
                    });
        }
    }

    @Nested
    @DisplayName("Save customer endpoint")
    class SaveCustomerTests {

        private static Stream<Arguments> customerAndDTO() {
            Customer customer = Bootstrap.customerFactory().get();

            CustomerDTO dto = new CustomerDTO(
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPassword(),
                    customer.getEmail(),
                    customer.getAddress()
            );

            return Stream.generate(() -> arguments(customer, dto)).limit(1);
        }

        @ParameterizedTest
        @MethodSource("customerAndDTO")
        @DisplayName("accept valid customerDTO")
        void acceptValidDTO(Customer customer, CustomerDTO customerDTO) throws Exception {
            when(mockRepo.saveCustomer(customer)).thenReturn(Result.success(customer));
            when(mockMapper.customerFromDTO(customerDTO)).thenReturn(customer);

            mockMvc.perform(post("/library/customer/saveCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully saved customer"))
                    .andExpect(header().exists("Location"));
        }

        @ParameterizedTest
        @MethodSource("customerAndDTO")
        @DisplayName("reject if email exists")
        void rejectInvalidEmail(Customer customer, CustomerDTO customerDTO) throws Exception {
            when(mockRepo.saveCustomer(customer))
                    .thenReturn(Result.failure(new IllegalArgumentException("Email already exists")));
            when(mockMapper.customerFromDTO(customerDTO)).thenReturn(customer);

            mockMvc.perform(post("/library/customer/saveCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Email already exists");
                    });
        }
    }
}