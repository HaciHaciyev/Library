package core.project.library.application.model;

import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AuthorDTO(@NotNull @Valid FirstName firstName,
                        @NotNull @Valid LastName lastName,
                        @NotNull @Valid Email email,
                        @NotNull @Valid Address address) {
}
