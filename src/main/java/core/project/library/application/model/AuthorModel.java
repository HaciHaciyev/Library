package core.project.library.application.model;

import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AuthorModel(@NotNull FirstName firstName,
                          @NotNull LastName lastName,
                          @NotNull Email email,
                          @NotNull Address address,
                          @NotNull Set<BookDTO> books) {
}
