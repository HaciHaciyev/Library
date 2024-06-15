package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstName(@NotBlank @Size(min = 3, max = 25) String firstName) {

    public FirstName {
        if (firstName == null) {
            throw new IllegalArgumentException("First name cannot be null");
        }
        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First Name should`t be blank.");
        }
        if (firstName.length() < 2 || firstName.length() > 25) {
            throw new IllegalArgumentException("Fist Name should`t be smaller than 3 characters and greater than 25." +
                    "\n First Name : " + firstName);
        }
    }
}
