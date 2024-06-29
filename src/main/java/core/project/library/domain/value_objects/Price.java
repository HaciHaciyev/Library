package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record Price(@NotNull Double price) {

    public Price {
        Objects.requireNonNull(price);

        if (price < 0.0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
