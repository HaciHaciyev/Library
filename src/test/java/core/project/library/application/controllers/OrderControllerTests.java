package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.DomainProviders;
import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.entities.Order;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.Result;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.CustomerRepository;
import core.project.library.infrastructure.repository.OrderRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureDataJdbc
public class OrderControllerTests {
//TODO
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookRepository bookRepository;

    @Autowired
    OrderRepository orderRepositoryActual;

    @MockBean(name = "orderRepositoryMock")
    OrderRepository mockOrderRepo;


    @MockBean
    CustomerRepository customerRepository;

    @Nested
    @DisplayName("Find by id endpoint")
    class FindByIdEndpointTest {

        private static Stream<Arguments> getOrder() {
            return Stream.generate(() -> arguments(DomainProviders.order().get())).limit(1);
        }

        @ParameterizedTest
        @MethodSource("getOrder")
        @DisplayName("Accept valid uuid")
        void acceptValidUuid(Order order) throws Exception {
//            when(mockOrderRepo.findById(order.getId())).thenReturn(Optional.of(order));

            mockMvc.perform(get("/library/order/findById/" + "cc44dcec-1a4f-472e-aa01-4cbc9b50bba4")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isNotEmpty());
        }

//        @Test
//        @DisplayName("Reject invalid id")
//        void rejectInvalidId() throws Exception {
//            when(mockOrderRepo.findById(any(UUID.class))).thenReturn(Optional.empty());
//
//            mockMvc.perform(get("/library/order/findById/" + UUID.randomUUID())
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound());
//        }
    }

    @Nested
    @DisplayName("Find by customer id endpoint")
    class FindByCustomerIdEndpointTest {

        private static Stream<Arguments> orderAndCustomerId() {
            List<Order> orders = Stream.generate(() -> DomainProviders.order().get()).limit(5).toList();
            return Stream.generate(() -> arguments(orders, UUID.randomUUID())).limit(1);
        }

//        @ParameterizedTest
//        @MethodSource("orderAndCustomerId")
//        @DisplayName("accept valid customer id")
//        void acceptValidCustomerId(List<Order> orders, UUID customerId) throws Exception {
//            when(mockOrderRepo.findByCustomerId(customerId)).thenReturn(orders);
//
//            mockMvc.perform(get("/library/order/findByCustomerId/" + customerId)
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$").isNotEmpty());
//        }

//        @Test
//        @DisplayName("reject invalid customer id")
//        void rejectInvalidCustomerId() throws Exception {
//            when(mockOrderRepo.findByCustomerId(any(UUID.class))).thenReturn(Collections.emptyList());
//
//            mockMvc.perform(get("/library/order/findByCustomerId/" + UUID.randomUUID())
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isEmpty());
//        }
    }

    @Nested
    @DisplayName("Find by book id endpoint")
    class FindByBookIdEndpointTest {

        private static Stream<Arguments> orderAndBookId() {
            List<Order> orders = Stream.generate(() -> DomainProviders.order().get()).limit(5).toList();
            return Stream.generate(() -> arguments(orders, UUID.randomUUID())).limit(1);
        }

        @ParameterizedTest
        @MethodSource("orderAndBookId")
        @DisplayName("Accept valid book id")
        void acceptValidBookId(List<Order> orders, UUID bookId) throws Exception {
            when(mockOrderRepo.findByBookId(bookId)).thenReturn(Result.success(orders));

            mockMvc.perform(get("/library/order/findByBookId/" + bookId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isNotEmpty());
        }

        @Test
        @DisplayName("reject invalid book id")
        void rejectInvalidBookId() throws Exception {
            when(mockOrderRepo.findByBookId(any(UUID.class))).thenReturn(Result.failure(null));

            mockMvc.perform(get("/library/order/findByBookId/" + UUID.randomUUID())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Create order endpoint")
    class CreateOrderEndpointTest {

        private static Stream<Arguments> customerAndBooks() {
            List<Book> books = Stream.generate(DomainProviders.book()).limit(5).toList();
            return Stream.generate(() -> arguments(Bootstrap.customerFactory().get(), books)).limit(1);
        }

        private static Stream<Arguments> getCustomer() {
            return Stream.of(arguments(Bootstrap.customerFactory().get()));
        }

        @ParameterizedTest
        @MethodSource("customerAndBooks")
        @DisplayName("Accept valid customer and book ids")
        void acceptValidCustomerAndBook(Customer customer, List<Book> books) throws Exception {
            when(customerRepository.findById(customer.getId())).thenReturn(Result.success(customer));
            books.forEach(book -> when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book)));

            List<UUID> bookIds = books.stream().map(Book::getId).toList();

            mockMvc.perform(post("/library/order/createOrder")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer))
                            .param("customerId", customer.getId().toString())
                            .param("booksId", bookIds.toString().replaceAll("[\\[\\]]", "")))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("getCustomer")
        @DisplayName("reject when customer does not exist")
        void rejectInvalidCustomerId(Customer customer) {
            MvcResult mvcResult = mockMvc.perform(post("/library/order/createOrder")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer))
                            .param("customerId", UUID.randomUUID().toString())
                            .param("booksId", UUID.randomUUID().toString()))
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("getCustomer")
        @DisplayName("reject when book does not exist")
        void rejectInvalidBookId(Customer customer) {
            MvcResult mvcResult = mockMvc.perform(post("/library/order/createOrder")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer))
                            .param("customerId", UUID.randomUUID().toString())
                            .param("booksId", UUID.randomUUID().toString()))
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }
}
