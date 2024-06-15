package core.project.library.application.controllers;

import core.project.library.application.mappers.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
class ControllerConfig {

    @Bean
    @Primary
    AuthorMapper authorMapper() {
        return new AuthorMapperImpl();
    }

    @Bean
    @Primary
    BookMapper bookMapper() {
        return new BookMapperImpl();
    }

    @Bean
    @Primary
    CustomerMapper customerMapper() {
        return new CustomerMapperImpl();
    }

    @Bean
    @Primary
    OrderMapper orderMapper() {
        return new OrderMapperImpl();
    }

    @Bean
    @Primary
    PublisherMapper publisherMapper() {
        return new PublisherMapperImpl();
    }
}
