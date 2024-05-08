package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.infrastructure.repositories.CustomerRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import core.project.library.infrastructure.repositories.sql_mappers.RowToCustomer;
import core.project.library.infrastructure.repositories.sql_mappers.RowToOrder;
import core.project.library.infrastructure.services.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
class CustomerTest {

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
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }
}