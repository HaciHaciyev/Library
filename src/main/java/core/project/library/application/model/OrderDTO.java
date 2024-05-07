package core.project.library.application.model;

import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.constraints.NotNull;

public record OrderDTO(@NotNull Integer countOfBooks,
                       @NotNull TotalPrice totalPrice) {
}
