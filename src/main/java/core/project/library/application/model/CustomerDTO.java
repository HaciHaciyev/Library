package core.project.library.application.model;

import core.project.library.domain.value_objects.*;
import jakarta.validation.constraints.NotNull;

public record CustomerDTO(@NotNull FirstName firstName,
                          @NotNull LastName lastName,
                          @NotNull Password password,
                          @NotNull Email email,
                          @NotNull Address address) {
}
