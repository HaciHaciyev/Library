package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotNull;

public record ChangeOfOrder(@NotNull Double changeOfOrder) {

    public ChangeOfOrder {
        if (changeOfOrder == null) {
            throw new NullValueException("Change can`t be null");
        }

        if (changeOfOrder < 0.0) {
            throw new NegativeValueException("Paid amount cannot be lower than total price of books");
        }
    }
}
