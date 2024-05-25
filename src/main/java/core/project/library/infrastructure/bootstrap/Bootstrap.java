package core.project.library.infrastructure.bootstrap;

import core.project.library.infrastructure.repository.BootstrapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final BootstrapRepository repository;

    public Bootstrap(BootstrapRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 1) repository.bootstrap();
        log.info("Bootstrap is completed. Basic values in database.");
    }
}