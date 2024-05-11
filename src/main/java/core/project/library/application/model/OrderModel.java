package core.project.library.application.model;

import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record OrderModel(@NotNull Integer countOfBooks,
                         @NotNull @Valid TotalPrice totalPrice,
                         @NotNull @Valid CustomerDTO customer,
                         @NotNull @Valid Set<BookDTO> books) {
}
