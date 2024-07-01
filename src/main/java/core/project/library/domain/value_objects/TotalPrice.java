package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotNull;

public record TotalPrice(@NotNull Double totalPrice) {

    public TotalPrice {
        if (totalPrice == null) {
            throw new NullValueException("Total price can`t be null");
        }
        if (totalPrice < 0.0) {
            throw new NegativeValueException("Price can`t be smaller than 0.");
        }
    }
}
