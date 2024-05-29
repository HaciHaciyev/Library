package core.project.library.infrastructure.service;

import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.repository.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublisherService {

    private final PublisherRepository repository;

    public PublisherService(PublisherRepository repository) {
        this.repository = repository;
    }

    public Optional<Publisher> findById(UUID publisherId) {
        return repository.findById(publisherId);
    }

    public Optional<List<Publisher>> findByName(String name) {
        return repository.findByName(name);
    }
}
