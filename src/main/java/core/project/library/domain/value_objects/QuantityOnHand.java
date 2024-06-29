package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record QuantityOnHand(@NotNull Integer quantityOnHand) {

    public QuantityOnHand {
        Objects.requireNonNull(quantityOnHand);

        if (quantityOnHand < 0) {
            throw new IllegalArgumentException("QuantityOnHand must be greater than or equal to 0");
        }
    }
}
