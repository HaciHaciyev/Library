package core.project.library.application.model;

import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookDTO(@NotNull Title title, @NotNull Description description, @NotNull ISBN isbn,
                      @NotNull BigDecimal price, @NotNull Integer quantityOnHand, @NotNull Category category) {
}
