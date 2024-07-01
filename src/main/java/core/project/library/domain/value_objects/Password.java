package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Password(@NotBlank @Size(min = 5, max = 48) String password) {

    public Password {
        if (password == null) {
            throw new NullValueException("Password can`t be null");
        }
        if (password.isBlank()) {
            throw new BlankValueException("Password should`t be blank.");
        }
        if (password.length() < 4 || password.length() > 48) {
            throw new InvalidSizeException("Password length should be greater than 4 characters and smaller than 48 characters.");
        }
    }
}
