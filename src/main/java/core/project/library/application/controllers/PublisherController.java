package core.project.library.application.controllers;

import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.Result;
import core.project.library.infrastructure.mappers.PublisherMapper;
import core.project.library.infrastructure.repository.PublisherRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
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
        var publisher = publisherRepository
                .findById(publisherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        return ResponseEntity.ok(publisherMapper.toDTO(publisher));
    }

    @GetMapping("/findByName/{publisherName}")
    final ResponseEntity<List<PublisherDTO>> findByName(@PathVariable("publisherName") String publisherName) {
        var publishers = publisherRepository
                .findByName(publisherName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        return ResponseEntity.ok(publisherMapper.listOfDTO(publishers));
    }

    @PostMapping("/savePublisher")
    final ResponseEntity<String> savePublisher(@RequestBody @Valid PublisherDTO publisherDTO) {
        Publisher publisher = publisherMapper.publisherFromDTO(publisherDTO);

        var publisherResult = publisherRepository.savePublisher(publisher);

        publisherResult.ifFailure(this::throwIfFailure);

        Publisher savedPublisher = publisherResult.value();

        return ResponseEntity
                .created(URI.create("/library/publisher/findById/" + savedPublisher.getId()))
                .body("Successfully saved publisher");
    }

    private void throwIfFailure(Exception e) {
        if (e instanceof IllegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save publisher");
        }
    }
}
