package core.project.library.application.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getBookById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/library/book/getBookById/d4f0aa27-317b-4e00-9462-9a7f0faa7a5e")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Book")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.isbn", is("978-161-729-045-9")))
                .andExpect(jsonPath("$.price", is("12.99")))
                .andExpect(jsonPath("$.quantityOnHand", is("43")))
                .andExpect(jsonPath("$.publisher.publisherName", is("Publisher")))
                .andExpect(jsonPath("$.authors[0].firstName", is("Author")))
                .andExpect(jsonPath("$.authors[0].email", is("author@gmail.com")))
                .andReturn();

        log.info(mvcResult.getResponse().getContentAsString());
    }
}