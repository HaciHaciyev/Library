package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TotalPrice(@NotNull BigDecimal totalPrice) {
}
