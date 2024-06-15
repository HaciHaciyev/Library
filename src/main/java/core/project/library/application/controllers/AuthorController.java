package core.project.library.application.controllers;

import core.project.library.application.mappers.AuthorMapper;
import core.project.library.application.model.AuthorDTO;
import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorMapper.toDTO(
                        authorRepository.findById(authorId).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/findByLastName/{authorLastName}")
    final ResponseEntity<List<AuthorDTO>> findByLastName(@PathVariable("authorLastName") String authorLastName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorMapper.listOfDTO(authorRepository
                        .findByLastName(authorLastName)
                        .orElseThrow(NotFoundException::new)));
    }

    @PostMapping("/saveAuthor")
    final ResponseEntity<Void> saveAuthor(@RequestBody @Valid AuthorDTO authorDTO) {
        if (authorRepository.isEmailExists(Objects.requireNonNull(authorDTO.email()))) {
            throw new IllegalArgumentException("Email was be used");
        }

        Author author = Author.builder()
                .id(UUID.randomUUID())
                .firstName(authorDTO.firstName())
                .lastName(authorDTO.lastName())
                .email(authorDTO.email())
                .address(authorDTO.address())
                .events(new Events())
                .build();

        authorRepository.saveAuthor(author);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", String.format("/library/author/findById/%s", author.getId().toString()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}
