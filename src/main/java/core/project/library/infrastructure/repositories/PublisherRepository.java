package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.repositories.sql_mappers.RowToPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Optional<Publisher> getPublisherById(UUID publisherId) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("Select * from Publisher where id=?", rowToPublisher, publisherId.toString())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Publisher> savePublisher(Publisher publisherForSave) {
        jdbcTemplate.update("""
        Insert into Publisher (id, publisher_name, state, city, street, home,
                       phone, email, creation_date, last_modified_date)
                       values (?,?,?,?,?,?,?,?,?,?)
        """,
                publisherForSave.getId().toString(), publisherForSave.getPublisherName().publisherName(),
                publisherForSave.getAddress().state(), publisherForSave.getAddress().city(),
                publisherForSave.getAddress().street(), publisherForSave.getAddress().home(),
                publisherForSave.getPhone().phoneNumber(), publisherForSave.getEmail().email(),
                publisherForSave.getEvents().creation_date(), publisherForSave.getEvents().last_update_date()
        );
        return Optional.of(publisherForSave);
    }
}
