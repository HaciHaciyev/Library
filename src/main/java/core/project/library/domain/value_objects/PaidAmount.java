package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;

public record PaidAmount(@NotNull Double paidAmount) {

    public PaidAmount {
        if (paidAmount == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (paidAmount < 0) {
            throw new IllegalArgumentException("Price can`t be smaller than 0.");
        }
    }
}
