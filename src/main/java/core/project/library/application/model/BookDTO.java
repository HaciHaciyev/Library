package core.project.library.application.model;

import core.project.library.domain.value_objects.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record BookDTO(@NotNull @Valid Title title,
                      @NotNull @Valid Description description,
                      @NotNull @Valid ISBN isbn,
                      @NotNull @Valid Price price,
                      @NotNull @Valid QuantityOnHand quantityOnHand,
                      @NotNull Category category) {
}
