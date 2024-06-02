package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.mappers.EntityMapperImpl;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.repository.CustomerRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerRepository customerRepository;

    static Faker faker = new Faker();

    @Nested
    @DisplayName("FindById endpoint")
    class FindByIdTests {

        private static final String FIND_BY_ID = "/library/customer/findById/";

        @ParameterizedTest
        @MethodSource("predefinedCustomer")
        @DisplayName("Accept predefined valid customer")
        void testFindById(Customer customer) throws Exception {
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

            mockMvc.perform(get(FIND_BY_ID + customer.getId().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @ParameterizedTest
        @MethodSource("randomCustomers")
        @DisplayName("Accept random customers")
        void testRandomCustomers(Customer randomCustomer) throws Exception {
            when(customerRepository.findById(randomCustomer.getId())).thenReturn(Optional.of(randomCustomer));
            mockMvc.perform(get(FIND_BY_ID + randomCustomer.getId().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

// эта штука временно не работает НЕ ТРОГАТЬ!!!
//        @ParameterizedTest
//        @MethodSource("invalidCustomer")
//        @DisplayName("Reject invalid customer")
//        void testInvalidCustomer(Customer invalid) throws Exception {
//            when(service.findById(invalid.getId())).thenReturn(Optional.of(invalid));
//            mockMvc.perform(get(FIND_BY_ID + invalid.getId().toString())
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//        }

        private static Stream<Arguments> predefinedCustomer() {
            return Stream.generate(() -> arguments(
                    Customer.builder()
                            .id(java.util.UUID.fromString("58e9909b-742f-4cd0-b1a1-0e8689d0fcfd"))
                            .firstName(new FirstName("Customer"))
                            .lastName(new LastName("Customerovich"))
                            .password(new Password("password"))
                            .email(new Email("customer@gmail.com"))
                            .address(new Address("State", "City", "Street", "Home"))
                            .events(new Events())
                            .build())).limit(1);
        }

        private static Stream<Arguments> randomCustomers() {
            return Stream.generate(() -> arguments(
                    Customer.builder()
                            .id(UUID.randomUUID())
                            .firstName(new FirstName(faker.name().firstName()))
                            .lastName(new LastName(faker.name().lastName()))
                            .password(new Password(faker.examplify("example")))
                            .email(new Email(faker.letterify("??????@gmail.com")))
                            .address(new Address(faker.address().state(), faker.address().city(),
                                    faker.address().streetAddress(), faker.address().secondaryAddress())
                            )
                            .events(new Events())
                            .build())).limit(5);
        }

//        private static Stream<Arguments> invalidCustomer() {
//            return null;
//        }
    }

    private static Stream<Arguments> listOfCustomersWithMatchingLastName() {
        String lastName = faker.name().lastName();

        Supplier<Customer> customerSupplier = () -> Customer.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName(faker.name().firstName()))
                .lastName(new LastName(lastName))
                .password(new Password(faker.examplify("example")))
                .email(new Email(faker.letterify("??????@gmail.com")))
                .address(new Address(faker.address().state(), faker.address().city(),
                        faker.address().streetAddress(), faker.address().secondaryAddress())
                )
                .events(new Events())
                .build();


        return Stream.generate(() -> arguments(
                        Stream.generate(customerSupplier).limit(5).toList()
                ))
                .limit(5);
    }

    @ParameterizedTest
    @MethodSource("listOfCustomersWithMatchingLastName")
    @DisplayName("Accept random customers with matching last name")
    void testFindByLastName(List<Customer> customerList) throws Exception {
        when(customerRepository.findByLastName("Customerovich")).thenReturn(Optional.of(customerList));
        mockMvc.perform(get("/library/customer/findByLastName/Customerovich")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @SpringBootApplication
    static class ControllerConfig {
        @Bean
        EntityMapper entityMapper() {
            return new EntityMapperImpl();
        }
    }
}




