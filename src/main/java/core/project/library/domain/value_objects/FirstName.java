package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record FirstName(@NotBlank @Size(min = 3, max = 25) String firstName) {

    public FirstName {
        Objects.requireNonNull(firstName);
        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First Name should`t be blank.");
        }
        if (firstName.length() < 3 || firstName.length() > 25) {
            throw new IllegalArgumentException("Fist Name should`t be smaller than 3 characters and greater than 25.");
        }
    }
}
