package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.PublisherDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.service.PublisherService;
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
    private final PublisherService service;

    public PublisherController(EntityMapper mapper, PublisherService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/findById/{id}")
    ResponseEntity<PublisherDTO> findById(@PathVariable("id") UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDto(service
                        .findById(id).orElseThrow(NotFoundException::new)));
    }

    @GetMapping("/findByName/{name}")
    ResponseEntity<List<PublisherDTO>> findByName(@PathVariable("name") String name) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service
                        .findByName(name)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(mapper::toDto).toList());
    }
}
