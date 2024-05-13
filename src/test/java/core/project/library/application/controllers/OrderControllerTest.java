package core.project.library.application.controllers;

import core.project.library.infrastructure.repositories.BookRepository;
import core.project.library.infrastructure.repositories.CustomerRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import core.project.library.infrastructure.repositories.sql_mappers.*;
import core.project.library.infrastructure.services.OrderService;
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
class OrderControllerTest {

    MockMvc mockMvc;
    @Autowired
    OrderController orderController;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    RowToAuthor rowToAuthor;
    @Autowired
    RowToBook rowToBook;
    @Autowired
    RowToPublisher rowToPublisher;
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
    void getOrderById() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/library/order/getOrderById/a486f288-cec3-4205-b753-d4ddf2796f9a")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.countOfBooks", is(1)))
                .andExpect(jsonPath("$.totalPrice.totalPrice", is(12.99)))
                .andExpect(jsonPath("$.customer.firstName.firstName", is("Customer")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getNotFoundOrderById() throws Exception{
        mockMvc.perform(get("/library/order/getOrderById/ad9026e0-616a-49a7-ac73-33b4b9436b40")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}