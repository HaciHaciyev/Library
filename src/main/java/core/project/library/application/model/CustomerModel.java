package core.project.library.application.model;

import core.project.library.domain.value_objects.*;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CustomerModel(@NotNull FirstName firstName,
                            @NotNull LastName lastName,
                            @NotNull Password password,
                            @NotNull Email email,
                            @NotNull Address address,
                            @NotNull Set<OrderDTO> orders) {
}
