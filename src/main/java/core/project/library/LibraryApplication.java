package core.project.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LibraryApplication.class);
        application.setAdditionalProfiles("dev");
        application.run(args);
    }
}
