package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public record TotalPrice(@NotNull BigDecimal totalPrice) {

    public TotalPrice {
        Objects.requireNonNull(totalPrice);
        if (totalPrice.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new IllegalArgumentException("Price can`t be smaller than 0.");
        }
    }
}
