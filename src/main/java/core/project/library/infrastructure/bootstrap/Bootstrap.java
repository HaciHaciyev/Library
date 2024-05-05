package core.project.library.infrastructure.bootstrap;

import core.project.library.infrastructure.repositories.BootstrapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final Optional<BootstrapRepository> repository;

    public Bootstrap(Optional<BootstrapRepository> repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Bootstrap is completed. Basic values in database.");
        repository.filter(repo -> repo.count() < 1).ifPresent(BootstrapRepository::bootstrap);
    }
}
