package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record Title(@NotBlank @Size(min = 3, max = 50) String title) {

    public Title {
        Objects.requireNonNull(title);
        if (title.isBlank()) {
            throw new IllegalArgumentException("Title should`t be blank.");
        }
        if (title.length() < 3 || title.length() > 55) {
            throw new IllegalArgumentException("Title should`t be longer than 50 characters and shorter than 5 characters." +
                    "\n Title: " + title);
        }
    }
}
