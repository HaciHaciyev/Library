package core.project.library.domain.value_objects;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class PublisherNameTest {

    @Test
    void invalidPublisherName() {
        try {
            PublisherName publisherName = new PublisherName(null);
            log.info(publisherName.toString());
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be null.");
        }

        try {
            PublisherName publisherName = new PublisherName("");
            log.info(publisherName.toString());
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be blank.");
        }

        try {
            PublisherName publisherName = new PublisherName("hell");
            log.info(publisherName.toString());
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be shorter than 5 characters.");
        }

        try {
            PublisherName publisherName = new PublisherName("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfgh");
            log.info(publisherName.toString());
        } catch (IllegalArgumentException e) {
            log.info("Publisher name can`t be longer than 25 characters.");
        }
    }

}