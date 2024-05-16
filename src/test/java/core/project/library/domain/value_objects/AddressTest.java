package core.project.library.domain.value_objects;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AddressTest {

    @Test
    void invalidAddress() {
        try {
            Address address = new Address(null, null, null, null);
        } catch (NullPointerException e) {
            log.info("Address cannot be null.");
        }

        try {
            Address address = new Address("", "assdd", "dasda", "rwewrw");
        } catch (IllegalArgumentException e) {
            log.info("Address cannot be blank.");
        }

        try {
            Address address = new Address("weruiqwyefhawjkkhr efhwilabl cifeu lawbglarhiumclahuilfha",
                    "dqwwqd", "dqwqdwq", "qdweqwe");
        } catch (IllegalArgumentException e) {
            log.info("Address to long.");
        }
    }
}