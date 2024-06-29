package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

public record TotalPrice(@NotNull Double totalPrice) {

    public TotalPrice {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (totalPrice < 0) {
            throw new IllegalArgumentException("Price can`t be smaller than 0.");
        }
    }
}
