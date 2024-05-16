package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record LastName(@NotBlank @Size(min = 3, max = 25) String lastName) {

    public LastName {
        Objects.requireNonNull(lastName);
        if (lastName.isBlank()) {
            throw new IllegalArgumentException("Last Name should`t be blank.");
        }
        if (lastName.length() < 3 || lastName.length() > 25) {
            throw new IllegalArgumentException("Last Name should be greater than 5 characters and smaller than 25.");
        }
    }
}
