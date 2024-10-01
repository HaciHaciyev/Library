package core.project.library.application.controllers;

import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.mappers.PublisherMapper;
import core.project.library.infrastructure.repository.PublisherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/publisher")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherMapper publisherMapper;

    private final PublisherRepository publisherRepository;

    @GetMapping("/findById/{publisherId}")
    final ResponseEntity<PublisherDTO> findById(@PathVariable("publisherId") UUID publisherId) {
        var publisher = publisherRepository
                .findById(publisherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        return ResponseEntity.ok(publisherMapper.toDTO(publisher));
    }

    @GetMapping("/findByName/{publisherName}")
    final ResponseEntity<List<PublisherDTO>> findByName(@PathVariable("publisherName") String publisherName) {
        var publishers = publisherRepository.findByName(publisherName);

        if (publishers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found");
        }

        return ResponseEntity.ok(publisherMapper.listOfDTO(publishers));
    }

    @PostMapping("/savePublisher")
    final ResponseEntity<String> savePublisher(@RequestBody @Valid PublisherDTO publisherDTO) {
        if (publisherRepository.emailExists(publisherDTO.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (publisherRepository.phoneExists(publisherDTO.phone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already exists");
        }

        Publisher publisher = publisherMapper.publisherFromDTO(publisherDTO);

        var savedPublisher = publisherRepository.savePublisher(publisher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save publisher"));

        return ResponseEntity
                .created(URI.create("/library/publisher/findById/" + savedPublisher.getId()))
                .body("Successfully saved publisher");
    }
}
