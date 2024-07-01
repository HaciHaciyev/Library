package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record QuantityOnHand(@NotNull Integer quantityOnHand) {

    public QuantityOnHand {
        if (Objects.isNull(quantityOnHand)) {
            throw new NullValueException("QuantityOnHand can`t be null");
        }
        if (quantityOnHand < 0) {
            throw new NegativeValueException("QuantityOnHand must be greater than or equal to 0");
        }
    }
}
