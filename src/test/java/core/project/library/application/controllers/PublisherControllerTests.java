package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.PublisherName;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.PublisherRepository;
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

import static core.project.library.infrastructure.utilities.Domain.publisher;
import static core.project.library.infrastructure.utilities.ValueObjects.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublisherController.class)
public class PublisherControllerTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PublisherRepository publisherRepository;

    @Nested
    @DisplayName("Find by id endpoint")
    class FindByIdEndpointTest {

        private static Stream<Arguments> getPublisher() {
            return Stream.of(arguments(publisher().get()));
        }

        @ParameterizedTest
        @MethodSource("getPublisher")
        @DisplayName("Accept valid publisher id")
        void acceptValidPublisherId(Publisher publisher) throws Exception {
            when(publisherRepository.findById(publisher.getId())).thenReturn(Optional.of(publisher));

            mockMvc.perform(get("/library/publisher/findById/" + publisher.getId())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isNotEmpty());
        }

        @Test
        @DisplayName("reject invalid id")
        void rejectInvalidId() throws Exception {
            when(publisherRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get("/library/publisher/findById/" + UUID.randomUUID())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Find by name endpoint")
    class FindByNameEndpointTest {

        private static Stream<Arguments> getPublishersByName() {
            PublisherName name = randomPublisherName();
            Supplier<Publisher> supplier = () -> Publisher.builder()
                    .id(UUID.randomUUID())
                    .publisherName(name)
                    .address(randomAddress())
                    .phone(randomPhone())
                    .email(randomEmail())
                    .events(new Events())
                    .build();

            List<Publisher> publishers = Stream.generate(supplier).limit(5).toList();
            return Stream.of(arguments(publishers, name.publisherName()));
        }

        @ParameterizedTest
        @MethodSource("getPublishersByName")
        @DisplayName("Accept publishers with same name")
        void acceptPublishersWithSameName(List<Publisher> publishers, String name) throws Exception {
            when(publisherRepository.findByName(name))
                    .thenReturn(Optional.of(publishers));

            mockMvc.perform(get("/library/publisher/findByName/" + name)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isNotEmpty());
        }

        @Test
        @DisplayName("reject when no publisher found")
        void rejectWhenNoPublisherFound() throws Exception {
            when(publisherRepository.findByName(anyString())).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get("/library/publisher/findByName/" + "random")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            assertThat(mvcResult.getResolvedException()).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Save publisher endpoint")
    class SavePublisherEndpointTest {

        private static Stream<Arguments> getPublisherDTO() {
            PublisherDTO dto = new PublisherDTO(
                    randomPublisherName(),
                    randomAddress(),
                    randomPhone(),
                    randomEmail()
            );

            return Stream.of(arguments(dto));
        }

        @ParameterizedTest
        @MethodSource("getPublisherDTO")
        @DisplayName("Accept valid DTO")
        void acceptValidDTO(PublisherDTO customerDTO) throws Exception {
            when(publisherRepository.isEmailExists(customerDTO.email())).thenReturn(false);
            when(publisherRepository.isPhoneExists(customerDTO.phone())).thenReturn(false);

            mockMvc.perform(post("/library/publisher/savePublisher")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }

        @ParameterizedTest
        @MethodSource("getPublisherDTO")
        @DisplayName("reject existing email")
        void rejectExistingEmail(PublisherDTO customerDTO) {
            when(publisherRepository.isEmailExists(customerDTO.email())).thenReturn(true);

            assertThatThrownBy(() ->
                    mockMvc.perform(post("/library/publisher/savePublisher")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO))))
                    .hasMessageContaining("This email is used.");
        }

        @ParameterizedTest
        @MethodSource("getPublisherDTO")
        @DisplayName("reject existing phone number")
        void rejectExistingPhoneNumber(PublisherDTO customerDTO) throws Exception {
            when(publisherRepository.isPhoneExists(customerDTO.phone())).thenReturn(true);

            assertThatThrownBy(() ->
                    mockMvc.perform(post("/library/publisher/savePublisher")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customerDTO))))
                    .hasMessageContaining("This phone is used.");
        }
    }
}
