package core.project.library;

import net.datafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LibraryApplication.class);
        application.setAdditionalProfiles("dev");
        application.run(args);
    }

    @Bean
    public Faker faker() {
        return new Faker();
    }
}
