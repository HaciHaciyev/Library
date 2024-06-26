package core.project.library.application.controllers;

import core.project.library.application.model.AuthorDTO;
import core.project.library.domain.entities.Author;
import core.project.library.infrastructure.mappers.AuthorMapper;
import core.project.library.infrastructure.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/library/author")
public class AuthorController {

    private final AuthorMapper authorMapper;

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorMapper authorMapper, AuthorRepository authorRepository) {
        this.authorMapper = authorMapper;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/findById/{authorId}")
    final ResponseEntity<AuthorDTO> findById(@PathVariable("authorId") UUID authorId) {
        var author = authorRepository
                .findById(authorId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Author's not found"));

        return ResponseEntity.ok(authorMapper.toDTO(author));
    }

    @GetMapping("/findByLastName/{authorLastName}")
    final ResponseEntity<List<AuthorDTO>> findByLastName(@PathVariable("authorLastName") String authorLastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorMapper
                        .listOfDTO(authorRepository
                        .findByLastName(authorLastName)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author's not found")))
                );
    }

    @PostMapping("/saveAuthor")
    final ResponseEntity<String> saveAuthor(@RequestBody @Valid AuthorDTO authorDTO) {
        if (authorRepository.emailExists(authorDTO.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Author author = authorMapper.authorFromDTO(authorDTO);

        var savedAuthor = authorRepository.saveAuthor(author)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save author"));

        return ResponseEntity
                .created(URI.create("/library/author/findById/" + savedAuthor.getId()))
                .body("Successfully saved author");
    }

}
