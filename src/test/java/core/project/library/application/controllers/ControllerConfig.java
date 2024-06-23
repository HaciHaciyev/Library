package core.project.library.application.controllers;

import core.project.library.infrastructure.mappers.*;
import core.project.library.infrastructure.repository.OrderRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Bean
    @Primary
    OrderRepository orderRepositoryActual(JdbcTemplate jdbcTemplate) {
        return new OrderRepository(jdbcTemplate);
    }

    @Bean
    OrderRepository orderRepositoryMock(JdbcTemplate jdbcTemplate) {
        return new OrderRepository(jdbcTemplate);
    }
}
