package core.project.library.application.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BookController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    void getBookByIdTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/findById/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("Title")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("9781861972712")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(43)))
                .andExpect(jsonPath("$.publisher.publisherName.publisherName", is("Publisher")))
                .andExpect(jsonPath("$.authors[0].firstName.firstName", is("Author")))
                .andExpect(jsonPath("$.authors[0].email.email", is("author@gmail.com")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void getBookByIdTestWithMultiplyAuthors() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/findById/b77380fb-ecde-41b3-ab6b-9cc935ca2d1c")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("BookForRow")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("9781861972712")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(43)))
                .andExpect(jsonPath("$.publisher.publisherName.publisherName", is("PublisherForRow")))
                .andExpect(jsonPath("$.authors[0].firstName.firstName", is("AuthorForRow")))
                .andExpect(jsonPath("$.authors[0].email.email", is("author@gmail.com")))
                .andExpect(jsonPath("$.authors[1].firstName.firstName", is("SecondAuthorForRow")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void getNotFoundExceptionInBookIdEndpoint() throws Exception {
        mockMvc.perform(get("/library/book/findById/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void getByTitle() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/findByTitle/Title")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("Title")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("9781861972712")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(43)))
                .andExpect(jsonPath("$.publisher.publisherName.publisherName", is("Publisher")))
                .andExpect(jsonPath("$.authors[0].firstName.firstName", is("Author")))
                .andExpect(jsonPath("$.authors[0].email.email", is("author@gmail.com")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(5)
    void getNotFoundExceptionInBookTitleEndpoint() throws Exception {
        mockMvc.perform(get("/library/book/findByTitle/Doesn`t_Exists")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    void listOfBooks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/library/book/pageOfBook?pageNumber=0&pageSize=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        log.info("Page: {}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(7)
    void listByCategory() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/pageOfBook?pageNumber=0&pageSize=10&category=Adventure")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        log.info("Page by Category: {}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(8)
    void listByAuthor() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/pageOfBook?pageNumber=0&pageSize=10&author=Authorovich")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        log.info("Page by Author: {}", mvcResult.getResponse().getContentAsString());
    }
}