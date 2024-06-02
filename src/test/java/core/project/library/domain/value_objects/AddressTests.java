package core.project.library.domain.value_objects;

import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AddressTests {

    private static final String VALID_STRING = "valid";
    public static final String INVALID_STRING = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm";

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("For null or empty state")
        void rejectNullOrEmptyState(String state) {
            assertThatException()
                    .isThrownBy(() -> new Address(state, VALID_STRING, VALID_STRING, VALID_STRING))
                    .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("For null or empty city")
        void rejectNullOrEmptyCity(String city) {
            assertThatException()
                    .isThrownBy(() -> new Address(VALID_STRING, city, VALID_STRING, VALID_STRING));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("For null or empty street")
        void rejectNullOrEmptyStreet(String street) {
            assertThatException()
                    .isThrownBy(() -> new Address(VALID_STRING, VALID_STRING, street, VALID_STRING));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("For null or empty home")
        void rejectNullOrEmptyHome(String home) {
            assertThatException()
                    .isThrownBy(() -> new Address(VALID_STRING, VALID_STRING, VALID_STRING, home));
        }

        @Test
        @DisplayName("For too long state")
        void rejectStateTooLong() {
            assertThatException()
                    .isThrownBy(() -> new Address(INVALID_STRING, VALID_STRING, VALID_STRING, VALID_STRING));
        }

        @Test
        @DisplayName("For too long city")
        void rejectCityTooLong() {
            assertThatException()
                    .isThrownBy(() -> new Address(VALID_STRING, INVALID_STRING, VALID_STRING, VALID_STRING));
        }

        @Test
        @DisplayName("For too long street ")
        void rejectStreetTooLong() {
            assertThatException()
                    .isThrownBy(() -> new Address(VALID_STRING, VALID_STRING, INVALID_STRING, VALID_STRING));
        }

        @Test
        @DisplayName("For too long home")
        void rejectHomeTooLong() {
            assertThatException()
                    .isThrownBy(() -> new Address(VALID_STRING, VALID_STRING, VALID_STRING, INVALID_STRING));
        }
    }


    static Faker faker = new Faker();

    static Stream<Arguments> randomValues() {
        return Stream.generate(() -> arguments(
                faker.address().country(),
                faker.address().city(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress())
        ).limit(10);
    }

    @ParameterizedTest
    @MethodSource("randomValues")
    @DisplayName("Addresses with random values")
    void shouldCreateAddressWithRandomValues(String state, String city, String street, String home) {
        assertThatNoException()
                .isThrownBy(() -> new Address(state, city, street, home));
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        public static final String EDGE_CASE = "qwertyuiopasdfghjklzxcvbn";

        @Test
        @DisplayName("For state")
        void shouldCreateAddressWithEdgeCaseState() {
            assertThatNoException()
                    .isThrownBy(() -> new Address(EDGE_CASE, VALID_STRING, VALID_STRING, VALID_STRING));
        }

        @Test
        @DisplayName("For city")
        void shouldCreateAddressWithEdgeCaseCity() {
            assertThatNoException()
                    .isThrownBy(() -> new Address(VALID_STRING, EDGE_CASE, VALID_STRING, VALID_STRING));
        }

        @Test
        @DisplayName("For street")
        void shouldCreateAddressWithEdgeCaseStreet() {
            assertThatNoException()
                    .isThrownBy(() -> new Address(VALID_STRING, VALID_STRING, EDGE_CASE, VALID_STRING));
        }

        @Test
        @DisplayName("For home")
        void shouldCreateAddressWithEdgeCaseHome() {
            assertThatNoException()
                    .isThrownBy(() -> new Address(VALID_STRING, VALID_STRING, VALID_STRING, EDGE_CASE));
        }
    }
}
