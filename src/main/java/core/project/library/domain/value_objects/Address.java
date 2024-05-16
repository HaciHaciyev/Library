package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record Address(@NotBlank @Size(max = 25) String state,
                      @NotBlank @Size(max = 25) String city,
                      @NotBlank @Size(max = 25) String street,
                      @NotBlank @Size(max = 25) String home) {

    public Address {
        validateToNullBlankAndSize(new Object[] {state, city, street, home});
    }

    private static void validateToNullBlankAndSize(Object[] o) {
        for (Object object : o) {
            Objects.requireNonNull(object);
            if (object instanceof String string) {
                if (string.isBlank()) {
                    throw new IllegalArgumentException("String should`t be blank.");
                }
                if (string.length() > 25) {
                    throw new IllegalArgumentException("String should be shorter than 25 characters.");
                }
            }
        }
    }
}
