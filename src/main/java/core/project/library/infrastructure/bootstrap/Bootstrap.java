package core.project.library.infrastructure.bootstrap;

import core.project.library.infrastructure.repositories.Repository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private final Repository repository;

    public Bootstrap(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < 1) {
            repository.save();
        }
    }
}
