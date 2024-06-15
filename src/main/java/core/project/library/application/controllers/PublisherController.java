package core.project.library.application.controllers;

import core.project.library.application.mappers.PublisherMapper;
import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.PublisherRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/library/publisher")
public class PublisherController {

    private final PublisherMapper publisherMapper;

    private final PublisherRepository publisherRepository;

    public PublisherController(PublisherMapper publisherMapper, PublisherRepository publisherRepository) {
        this.publisherMapper = publisherMapper;
        this.publisherRepository = publisherRepository;
    }

    @GetMapping("/findById/{publisherId}")
    final ResponseEntity<PublisherDTO> findById(@PathVariable("publisherId") UUID publisherId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(publisherMapper.toDTO(
                        publisherRepository.findById(publisherId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByName/{publisherName}")
    final ResponseEntity<List<PublisherDTO>> findByName(@PathVariable("publisherName") String publisherName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(publisherMapper.listOfDTO(publisherRepository
                        .findByName(publisherName)
                        .orElseThrow(NotFoundException::new)));
    }

    @PostMapping("/createPublisher")
    final ResponseEntity<Void> createPublisher(@RequestBody @Valid PublisherDTO publisherDTO) {
        if (publisherRepository.isEmailExists(Objects.requireNonNull(publisherDTO.email()))) {
            throw new IllegalArgumentException("This email is used.");
        }

        if (publisherRepository.isPhoneExists(publisherDTO.phone())) {
            throw new IllegalArgumentException("This phone is used.");
        }

        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(publisherDTO.publisherName())
                .address(publisherDTO.address())
                .phone(publisherDTO.phone())
                .email(publisherDTO.email())
                .events(new Events())
                .build();

        publisherRepository.savePublisher(publisher);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", String.format("/library/publisher/findById/%s", publisher.getId().toString()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
