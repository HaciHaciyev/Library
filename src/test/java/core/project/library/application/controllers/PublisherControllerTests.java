package core.project.library.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.project.library.application.bootstrap.Bootstrap;
import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.PublisherName;
import core.project.library.infrastructure.exceptions.Result;
import core.project.library.infrastructure.mappers.PublisherMapper;
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

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static core.project.library.application.bootstrap.Bootstrap.publisherFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
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
    PublisherRepository mockRepo;

    @MockBean
    PublisherMapper mockMapper;

    @Nested
    @DisplayName("Find by id endpoint")
    class FindByIdEndpointTest {

        public static final String FIND_BY_ID = "/library/publisher/findById/";

        private static Stream<Arguments> publisherAndDTO() {
            Publisher publisher = publisherFactory().get();

            PublisherDTO dto = new PublisherDTO(
                    publisher.getPublisherName(),
                    publisher.getAddress(),
                    publisher.getPhone(),
                    publisher.getEmail()
            );

            return Stream.of(arguments(publisher, dto));
        }

        @ParameterizedTest
        @MethodSource("publisherAndDTO")
        @DisplayName("Accept valid publisher id")
        void acceptValidPublisherId(Publisher publisher, PublisherDTO publisherDTO) throws Exception {
            when(mockRepo.findById(publisher.getId())).thenReturn(Result.success(publisher));
            when(mockMapper.toDTO(publisher)).thenReturn(publisherDTO);

            mockMvc.perform(get(FIND_BY_ID + publisher.getId().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.publisherName.publisherName", is(publisher.getPublisherName().publisherName())),
                            jsonPath("$.email.email", is(publisher.getEmail().email())),
                            jsonPath("$.phone.phoneNumber", is(publisher.getPhone().phoneNumber())),
                            jsonPath("$.address.state", is(publisher.getAddress().state())),
                            jsonPath("$.address.city", is(publisher.getAddress().city())),
                            jsonPath("$.address.street", is(publisher.getAddress().street())),
                            jsonPath("$.address.home", is(publisher.getAddress().home()))
                    );

        }

        @Test
        @DisplayName("reject invalid id")
        void rejectInvalidId() throws Exception {
            when(mockRepo.findById(any(UUID.class))).thenReturn(Result.failure(null));

            mockMvc.perform(get(FIND_BY_ID + UUID.randomUUID())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Publisher not found");
                    });
        }
    }

    @Nested
    @DisplayName("Find by name endpoint")
    class FindByNameEndpointTest {

        public static final String FIND_BY_NAME = "/library/publisher/findByName/";

        private static Stream<Arguments> publishersDtosName() {
            PublisherName name = Bootstrap.randomPublisherName();

            Supplier<Publisher> supplier = () -> Publisher.builder()
                    .id(UUID.randomUUID())
                    .publisherName(name)
                    .address(Bootstrap.randomAddress())
                    .phone(Bootstrap.randomPhone())
                    .email(Bootstrap.randomEmail())
                    .events(new Events())
                    .build();

            List<Publisher> publishers = Stream.generate(supplier).limit(5).toList();

            List<PublisherDTO> dtos = publishers.stream()
                    .map(publisher -> new PublisherDTO(
                            publisher.getPublisherName(),
                            publisher.getAddress(),
                            publisher.getPhone(),
                            publisher.getEmail()))
                    .toList();

            return Stream.of(arguments(publishers, dtos, name.publisherName()));
        }

        @ParameterizedTest
        @MethodSource("publishersDtosName")
        @DisplayName("Accept publishers with same name")
        void acceptPublishersWithSameName(List<Publisher> publishers,
                                          List<PublisherDTO> dtos,
                                          String name) throws Exception {

            when(mockRepo.findByName(name)).thenReturn(Result.success(publishers));
            when(mockMapper.listOfDTO(publishers)).thenReturn(dtos);

            mockMvc.perform(get(FIND_BY_NAME + name)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.[*].publisherName.publisherName", everyItem(is(name)))
                    );
        }

        @Test
        @DisplayName("reject when no publisher found")
        void rejectWhenNoPublisherFound() throws Exception {
            String name = "randomName";
            when(mockRepo.findByName(name)).thenReturn(Result.failure(null));

            mockMvc.perform(get(FIND_BY_NAME + name)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Publisher not found");
                    });
        }
    }

    @Nested
    @DisplayName("Save publisher endpoint")
    class SavePublisherEndpointTest {

        public static final String SAVE_PUBLISHER = "/library/publisher/savePublisher";

        private static Stream<Arguments> publisherAndDTO() {
            Publisher publisher = publisherFactory().get();

            PublisherDTO dto = new PublisherDTO(
                    publisher.getPublisherName(),
                    publisher.getAddress(),
                    publisher.getPhone(),
                    publisher.getEmail()
            );

            return Stream.generate(() -> arguments(publisher, dto)).limit(1);
        }

        @ParameterizedTest
        @MethodSource("publisherAndDTO")
        @DisplayName("Accept valid DTO")
        void acceptValidDTO(Publisher publisher, PublisherDTO publisherDTO) throws Exception {
            when(mockRepo.savePublisher(publisher)).thenReturn(Result.success(publisher));
            when(mockMapper.publisherFromDTO(publisherDTO)).thenReturn(publisher);

            mockMvc.perform(post(SAVE_PUBLISHER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(publisherDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully saved publisher"))
                    .andExpect(header().exists("Location"));
        }

        @ParameterizedTest
        @MethodSource("publisherAndDTO")
        @DisplayName("reject existing email")
        void rejectExistingEmail(Publisher publisher, PublisherDTO publisherDTO) throws Exception {
            when(mockRepo.emailExists(any())).thenReturn(true);
            when(mockMapper.publisherFromDTO(publisherDTO)).thenReturn(publisher);

            mockMvc.perform(post(SAVE_PUBLISHER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(publisherDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Email already exists");
                    });
        }

        @ParameterizedTest
        @MethodSource("publisherAndDTO")
        @DisplayName("reject existing phone number")
        void rejectExistingPhoneNumber(Publisher publisher, PublisherDTO publisherDTO) throws Exception {
            when(mockRepo.phoneExists(any())).thenReturn(true);
            when(mockMapper.publisherFromDTO(publisherDTO)).thenReturn(publisher);

            mockMvc.perform(post(SAVE_PUBLISHER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(publisherDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(result -> {
                        String error = result.getResolvedException().getMessage();
                        assertThat(error).contains("Phone already exists");
                    });
        }
    }
}
