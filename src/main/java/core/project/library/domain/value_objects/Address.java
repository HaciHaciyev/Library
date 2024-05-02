package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(@NotBlank @Size(max = 25) String state,
                      @NotBlank @Size(max = 25) String city,
                      @NotBlank @Size(max = 25) String street,
                      @NotBlank @Size(max = 25) String home) {
}
