package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Description(@NotBlank @Size(min = 10, max = 255) String description) {

    public Description {
        if (description == null) {
            throw new NullValueException("Description cannot be null");
        }
        if (description.isBlank()) {
            throw new BlankValueException("Description should`t be blank.");
        }
        if (description.length() < 5 || description.length() > 255) {
            throw new InvalidSizeException("Description should be greater than 5 and shorter than 255 characters.");
        }
    }
}
