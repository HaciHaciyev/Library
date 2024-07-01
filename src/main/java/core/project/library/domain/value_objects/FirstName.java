package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstName(@NotBlank @Size(min = 3, max = 25) String firstName) {

    public FirstName {
        if (firstName == null) {
            throw new NullValueException("First name can`t be null");
        }
        if (firstName.isBlank()) {
            throw new BlankValueException("First Name should`t be blank.");
        }
        if (firstName.length() < 2 || firstName.length() > 25) {
            throw new InvalidSizeException("Fist Name should`t be smaller than 3 characters and greater than 25.");
        }
    }
}
