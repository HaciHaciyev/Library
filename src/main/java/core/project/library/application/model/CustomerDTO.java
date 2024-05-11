package core.project.library.application.model;

import core.project.library.domain.value_objects.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CustomerDTO(@NotNull @Valid FirstName firstName,
                          @NotNull @Valid LastName lastName,
                          @NotNull @Valid Password password,
                          @NotNull @Valid Email email,
                          @NotNull @Valid Address address) {
}
