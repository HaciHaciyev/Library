package core.project.library.domain.value_objects;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

@Slf4j
class PhoneTests {

    private static final Faker faker = new Faker();

    @RepeatedTest(10)
    @DisplayName("Phone number tests")
    void phoneNumberTests() {
        String phoneNumber = faker.phoneNumber().phoneNumber();
        String phoneNumberInternational = faker.phoneNumber().phoneNumberInternational();
        String phoneNumberNational = faker.phoneNumber().phoneNumberNational();
        String cellPhone = faker.phoneNumber().cellPhone();

        Assertions.assertAll(() -> new Phone(phoneNumber),
                () -> new Phone(phoneNumberInternational),
                () -> new Phone(phoneNumberNational),
                () -> new Phone(cellPhone));
    }
}
