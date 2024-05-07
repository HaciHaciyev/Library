package core.project.library.application.model;

import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record OrderModel(@NotNull Integer countOfBooks,
                         @NotNull TotalPrice totalPrice,
                         @NotNull CustomerDTO customer,
                         @NotNull Set<BookDTO> books) {
}
