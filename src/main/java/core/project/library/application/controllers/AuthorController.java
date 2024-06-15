package core.project.library.application.controllers;

import core.project.library.application.mappers.AuthorMapper;
import core.project.library.application.model.AuthorDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.AuthorRepository;
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

    private final AuthorMapper mapper;

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorMapper mapper, AuthorRepository authorRepository) {
        this.mapper = mapper;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/findById/{authorId}")
    final ResponseEntity<AuthorDTO> findById(@PathVariable("authorId") UUID authorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.dtoFrom(
                        authorRepository.findById(authorId).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/findByLastName/{authorLastName}")
    final ResponseEntity<List<AuthorDTO>> findByLastName(@PathVariable("authorLastName") String authorLastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.dtosFrom(authorRepository
                        .findByLastName(authorLastName)
                        .orElseThrow(NotFoundException::new)));
    }
}
