package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Title(@NotBlank @Size(min = 3, max = 50) String title) {

    public Title {
        if (title == null) {
            throw new NullValueException("Title can`t be null");
        }
        if (title.isBlank()) {
            throw new BlankValueException("Title should`t be blank.");
        }
        if (title.length() < 3 || title.length() > 55) {
            throw new InvalidSizeException("Title should`t be longer than 50 characters and shorter than 5 characters.");
        }
    }
}
