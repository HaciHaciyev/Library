package core.project.library.infrastructure.bootstrap;

import core.project.library.infrastructure.repositories.BootstrapRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Bootstrap implements CommandLineRunner {

    private final Optional<BootstrapRepository> repository;

    public Bootstrap(Optional<BootstrapRepository> repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        repository.filter(repo -> repo.count() < 1).ifPresent(BootstrapRepository::bootstrap);
    }
}
