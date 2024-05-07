package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.sql_mappers.RowToPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PublisherRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowToPublisher rowToPublisher;

    public PublisherRepository(JdbcTemplate jdbcTemplate, RowToPublisher rowToPublisher) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowToPublisher = rowToPublisher;
    }

    public Optional<Publisher> getPublisherByBookId(String bookId) {
        Optional<UUID> publisherId = Optional.ofNullable(jdbcTemplate
                .queryForObject("Select publisher_id from Book_Publisher where book_id=?",
                        UUID.class, bookId));

        return Optional.ofNullable(jdbcTemplate
                .queryForObject("Select * from Publisher where id=?",
                        rowToPublisher, publisherId.orElseThrow(NotFoundException::new))
        );
    }
}
