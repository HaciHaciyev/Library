package core.project.library.application.controllers;

import core.project.library.infrastructure.services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Test
    void getBookById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/getBookById/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title.title", is("Book")))
                .andExpect(jsonPath("$.description.description", is("Description")))
                .andExpect(jsonPath("$.isbn.isbn", is("978-161-729-045-9")))
                .andExpect(jsonPath("$.price", is("12.99")))
                .andExpect(jsonPath("$.quantityOnHand", is("43")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }
}