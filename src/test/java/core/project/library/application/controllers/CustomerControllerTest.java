package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.CustomerModel;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.repositories.CustomerRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import core.project.library.infrastructure.repositories.sql_mappers.RowToCustomer;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import core.project.library.infrastructure.services.CustomerService;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
class CustomerControllerTest {

    MockMvc mockMvc;
    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerService customerService;
    @Autowired
    EntityMapper entityMapper;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RowToOrder rowToOrder;
    @Autowired
    RowToCustomer rowToCustomer;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Faker faker;
    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .build();
    }

    @Test
    void getCustomerById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/customer/getCustomerById/58e9909b-742f-4cd0-b1a1-0e8689d0fcfd")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName.firstName", is("Customer")))
                .andExpect(jsonPath("$.orders[0].countOfBooks", is(1)))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void saveCustomer_ValidCustomer() throws Exception {
        CustomerModel model = entityMapper.toModel(getValidCustomer());
        mockMvc.perform(post("/library/customer/saveCustomer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateCustomer_ValidCustomer() throws Exception {
        CustomerModel validModel = entityMapper.toModel(getValidCustomer());
        mockMvc.perform(put("/library/customer/updateCustomer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validModel)))
                .andExpect(status().isCreated());
    }

    private Customer getValidCustomer() {
        return Customer.builder()
                .id(UUID.randomUUID())
                .firstName(new FirstName(faker.name().firstName()))
                .lastName(new LastName(faker.name().lastName()))
                .password(new Password(faker.numerify("########")))
                .email(new Email(faker.letterify("????????@gmail.com")))
                .address(new Address(faker.address().state(),
                        faker.address().city(),
                        faker.address().streetAddress(),
                        faker.address().buildingNumber()))
                .events(new Events())
                .build();
    }
}