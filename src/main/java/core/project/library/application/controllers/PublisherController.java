package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.PublisherDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/publisher")
public class PublisherController {

    private final EntityMapper mapper;

    private final PublisherRepository publisherRepository;

    public PublisherController(EntityMapper mapper, PublisherRepository publisherRepository) {
        this.mapper = mapper;
        this.publisherRepository = publisherRepository;
    }

    @GetMapping("/findById/{publisherId}")
    ResponseEntity<PublisherDTO> findById(@PathVariable("publisherId") UUID publisherId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDto(
                        publisherRepository.findById(publisherId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByName/{publisherName}")
    ResponseEntity<List<PublisherDTO>> findByName(@PathVariable("publisherName") String publisherName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(publisherRepository
                        .findByName(publisherName)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(mapper::toDto).toList());
    }
}
