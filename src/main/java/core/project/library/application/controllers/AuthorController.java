package core.project.library.application.controllers;

import core.project.library.application.model.AuthorDTO;
import core.project.library.domain.entities.Author;
import core.project.library.infrastructure.exceptions.Result;
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
        var authors = authorRepository
                .findByLastName(authorLastName)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Author's not found"));

        return ResponseEntity.ok(authorMapper.listOfDTO(authors));
    }

    @PostMapping("/saveAuthor")
    final ResponseEntity<String> saveAuthor(@RequestBody @Valid AuthorDTO authorDTO) {
        Author author = authorMapper.authorFromDTO(authorDTO);

        var authorResult = authorRepository.saveAuthor(author);

        authorResult.ifFailure(this::throwIfFailure);

        Author savedAuthor = authorResult.value();

        return ResponseEntity
                .created(URI.create("/library/author/findById/" + savedAuthor.getId()))
                .body("Successfully saved author");
    }

    private void throwIfFailure(Exception e) {
        if (e instanceof IllegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save author");
        }
    }
}
