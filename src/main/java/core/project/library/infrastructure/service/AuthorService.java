package core.project.library.infrastructure.service;

import core.project.library.domain.entities.Author;
import core.project.library.infrastructure.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public final Optional<Author> findById(UUID authorId) {
        return authorRepository.findById(authorId);
    }

    public final Optional<List<Author>> findByLastName(String lastName) {
        return authorRepository.findByLastName(lastName);
    }
}
