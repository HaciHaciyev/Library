package core.project.library.application.model;

import core.project.library.domain.value_objects.ChangeOfOrder;
import core.project.library.domain.value_objects.CreditCard;
import core.project.library.domain.value_objects.PaidAmount;
import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record OrderModel(@NotNull @Valid TotalPrice totalPrice,
                         @NotNull @Valid PaidAmount paidAmount,
                         @NotNull @Valid ChangeOfOrder changeOfOrder,
                         @NotNull @Valid CreditCard creditCard,
                         @NotNull @Valid CustomerDTO customer,
                         @NotNull @Valid Map<BookDTO, Integer> books) {
}
