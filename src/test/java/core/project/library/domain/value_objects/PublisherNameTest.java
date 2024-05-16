package core.project.library.domain.value_objects;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class PublisherNameTest {

    @Test
    void invalidPublisherName() {
        try {
            PublisherName publisherName = new PublisherName(null);
        } catch (NullPointerException e) {
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

    @Test
    void validPublisherName() {
        PublisherName publisherName = new PublisherName("Publisher");
        assertThat(publisherName)
                .isNotNull()
                .isEqualTo(new PublisherName("Publisher"));
        assertThat(publisherName.publisherName()).isEqualTo("Publisher");
    }
}