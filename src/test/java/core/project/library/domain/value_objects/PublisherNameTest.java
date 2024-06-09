package core.project.library.domain.value_objects;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class PublisherNameTest {

    @Test
    void invalidPublisherName() {
        try {
            PublisherName publisherName = new PublisherName(null);
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be null.");
        }

        try {
            PublisherName publisherName = new PublisherName("");
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be blank.");
        }

        try {
            PublisherName publisherName = new PublisherName("hell");
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be shorter than 5 characters.");
        }

        try {
            PublisherName publisherName = new PublisherName("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfgh");
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be longer than 25 characters.");
        }
    }

}