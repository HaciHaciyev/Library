package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Password(@NotBlank @Size(max = 48) String password) {
}
