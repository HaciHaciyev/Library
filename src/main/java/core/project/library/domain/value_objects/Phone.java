package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

public record Phone(@NotBlank String phoneNumber) {
}
