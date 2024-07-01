package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotNull;

public record PaidAmount(@NotNull Double paidAmount) {

    public PaidAmount {
        if (paidAmount == null) {
            throw new NullValueException("Total price can`t be null");
        }
        if (paidAmount < 0.0) {
            throw new NegativeValueException("Price can`t be smaller than 0.");
        }
    }
}
