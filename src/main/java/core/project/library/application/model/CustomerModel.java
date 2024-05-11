package core.project.library.application.model;

import core.project.library.domain.value_objects.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CustomerModel(@NotNull @Valid FirstName firstName,
                            @NotNull @Valid LastName lastName,
                            @NotNull @Valid Password password,
                            @NotNull @Valid Email email,
                            @NotNull @Valid Address address,
                            @NotNull @Valid Set<OrderDTO> orders) {
}
