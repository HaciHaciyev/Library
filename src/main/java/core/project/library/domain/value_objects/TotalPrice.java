package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TotalPrice(@NotNull BigDecimal totalPrice) {

    public TotalPrice {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (totalPrice.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new IllegalArgumentException("Price can`t be smaller than 0.");
        }
    }
}
