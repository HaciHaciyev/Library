package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

public record Email(@NotBlank
                    @jakarta.validation.constraints.Email
                    String email) {
}
