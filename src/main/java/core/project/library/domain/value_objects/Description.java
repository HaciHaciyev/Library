package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Description(@NotBlank @Size(max = 255) String description) {

}
