package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.mappers.EntityMapperImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
class ControllerConfig {
    @Bean
    @Primary
    EntityMapper entityMapper() {
        return new EntityMapperImpl();
    }
}
