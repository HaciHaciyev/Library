package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record Address(@NotBlank @Size(max = 25) String state,
                      @NotBlank @Size(max = 25) String city,
                      @NotBlank @Size(max = 25) String street,
                      @NotBlank @Size(max = 25) String home) {

    public Address(String state, String city, String street, String home) {
        validateToNullBlankAndSize(new Object[] {state, city, street, home});
        this.state = state;
        this.city = city;
        this.street = street;
        this.home = home;
    }

    private static void validateToNullBlankAndSize(Object[] o) {
        for (Object object : o) {
            Objects.requireNonNull(object);
            if (object instanceof String) {
                if (((String) object).isBlank()) {
                    throw new IllegalArgumentException("String should`t be blank.");
                }
                if (((String) object).length() > 25) {
                    throw new IllegalArgumentException("String should be shorter than 25 characters.");
                }
            }
        }
    }
}
