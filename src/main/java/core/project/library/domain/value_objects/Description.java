package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record Description(@NotBlank @Size(min = 10, max = 255) String description) {

    public Description {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("Description should`t be blank.");
        }
        if (description.length() < 5 || description.length() > 255) {
            throw new IllegalArgumentException("Description should be greater than 5 and shorter than 255 characters." +
                    "\n Description: " + description);
        }
    }
}
