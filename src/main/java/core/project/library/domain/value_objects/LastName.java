package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LastName(@NotBlank @Size(min = 3, max = 25) String lastName) {

    public LastName {
        if (lastName == null) {
            throw new NullValueException("Last Name cannot be null");
        }
        if (lastName.isBlank()) {
            throw new BlankValueException("Last Name should`t be blank.");
        }
        if (lastName.length() < 3 || lastName.length() > 25) {
            throw new InvalidSizeException("Last Name should be greater than 5 characters and smaller than 25.");
        }
    }
}
