package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.datafaker.Faker;

import java.util.Objects;

public record Address(@NotBlank @Size(max = 51) String state,
                      @NotBlank @Size(max = 51) String city,
                      @NotBlank @Size(max = 51) String street,
                      @NotBlank @Size(max = 51) String home) {

    public Address {
        validateToNullBlankAndSize(new Object[] {state, city, street, home});
    }

    public static Address randomInstance() {
        Faker faker = new Faker();
        return new Address(
                faker.address().state(),
                faker.address().city(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress()
        );
    }

    private static void validateToNullBlankAndSize(Object[] o) {
        for (Object object : o) {
            Objects.requireNonNull(object);
            if (object instanceof String string) {
                if (string.isBlank()) {
                    throw new IllegalArgumentException("String should`t be blank.");
                }
                if (string.length() > 51) {
                    throw new IllegalArgumentException("String should be shorter than 51 characters.");
                }
            }
        }
    }
}
