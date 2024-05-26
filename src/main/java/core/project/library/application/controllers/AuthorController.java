package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.AuthorDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/author")
public class AuthorController {

    private final EntityMapper entityMapper;

    private final AuthorService authorService;

    public AuthorController(EntityMapper entityMapper, AuthorService authorService) {
        this.entityMapper = entityMapper;
        this.authorService = authorService;
    }

    @GetMapping("/findById/{authorId}")
    public final ResponseEntity<AuthorDTO> findById(@PathVariable("authorId") UUID authorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toDTO(
                        authorService.findById(authorId).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/findByLastName/{lastName}")
    public final ResponseEntity<List<AuthorDTO>> findByLastName(@PathVariable("lastName") String lastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorService
                        .findByLastName(lastName)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(entityMapper::toDTO).toList()
                );
    }
}
