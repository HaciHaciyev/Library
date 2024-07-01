package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record Price(@NotNull Double price) {

    public Price {
        if (Objects.isNull(price)) {
            throw new NullValueException("Price can`t be null");
        }
        if (price < 0.0) {
            throw new NegativeValueException("Price can`t be negative");
        }
    }
}
