package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.application.model.CustomerDTO;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.LastName;
import core.project.library.infrastructure.exceptions.NotFoundException;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
    CustomerRepository customerRepository;

    @Nested
    @DisplayName("FindById endpoint")
    class FindByIdTests {

        private static final String FIND_BY_ID = "/library/customer/findById/";

        private static Stream<Arguments> randomCustomer() {
            Supplier<Customer> customerSupplier = () -> Customer.builder()
                    .id(UUID.randomUUID())
                    .firstName(Bootstrap.randomFirstName())
                    .lastName(Bootstrap.randomLastName())
                    .password(Bootstrap.randomPassword())
                    .email(Bootstrap.randomEmail())
                    .address(Bootstrap.randomAddress())
                    .events(new Events())
                    .build();

            return Stream.generate(() -> arguments(customerSupplier.get()))
                    .limit(1);
        }

        @ParameterizedTest
        @MethodSource("randomCustomer")
        @DisplayName("Accept UUID of existing customer")
        void testExisting(Customer customer) throws Exception {
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

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
            UUID nonExistent = UUID.randomUUID();
            when(customerRepository.findById(nonExistent)).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get(FIND_BY_ID + nonExistent)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("FindByLastName endpoint")
    class FindByLastNameTests {

        private static final String FIND_BY_LAST_NAME = "/library/customer/findByLastName/";

        private static Stream<Arguments> customersWithSameLastName() {
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

            return Stream.generate(() -> arguments(customers, lastName))
                    .limit(5);
        }

        @ParameterizedTest
        @MethodSource("customersWithSameLastName")
        @DisplayName("Return customers with matching last name")
        void testWithMatchingName(List<Customer> customerList, String lastName) throws Exception {
            when(customerRepository.findByLastName(lastName))
                    .thenReturn(Optional.of(customerList));

            mockMvc.perform(get(FIND_BY_LAST_NAME + lastName)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.[0].lastName.lastName", is(lastName)),
                            jsonPath("$.[1].lastName.lastName", is(lastName)),
                            jsonPath("$.[2].lastName.lastName", is(lastName)),
                            jsonPath("$.[3].lastName.lastName", is(lastName)),
                            jsonPath("$.[4].lastName.lastName", is(lastName))
                    );
        }

        @Test
        @DisplayName("Throw exception in case of no match")
        void testNoMatch() throws Exception {
            String lastName = "lastName";
            when(customerRepository.findByLastName(lastName)).thenReturn(Optional.empty());


            MvcResult mvcResult = mockMvc.perform(get(FIND_BY_LAST_NAME + lastName)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Save customer endpoint")
    class SaveCustomerTests {

        private static Stream<Arguments> validDTO() {
            CustomerDTO dto = new CustomerDTO(
                    Bootstrap.randomFirstName(),
                    Bootstrap.randomLastName(),
                    Bootstrap.randomPassword(),
                    Bootstrap.randomEmail(),
                    Bootstrap.randomAddress()
            );

            return Stream.generate(() -> arguments(dto)).limit(1);
        }

        @ParameterizedTest
        @MethodSource("validDTO")
        @DisplayName("accept valid customerDTO")
        void acceptValidDTO(CustomerDTO customerDTO) throws Exception {
            when(customerRepository.isEmailExists(customerDTO.email())).thenReturn(false);

            mockMvc.perform(post("/library/customer/saveCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }

        @ParameterizedTest
        @MethodSource("validDTO")
        @DisplayName("reject if email exists")
        void rejectInvalidEmail(CustomerDTO customerDTO) {
            when(customerRepository.isEmailExists(customerDTO.email())).thenReturn(true);

            assertThatThrownBy(() ->
                    mockMvc.perform(post("/library/customer/saveCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO))))
                    .hasMessageContaining("Email was be used");
        }
    }
}