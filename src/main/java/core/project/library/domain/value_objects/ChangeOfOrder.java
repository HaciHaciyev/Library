package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record ChangeOfOrder(@NotNull Double changeOfOrder) {

    public ChangeOfOrder {
        Objects.requireNonNull(changeOfOrder);

        if (changeOfOrder < 0) {
            throw new IllegalArgumentException("Change of order cannot be negative");
        }
    }
}
