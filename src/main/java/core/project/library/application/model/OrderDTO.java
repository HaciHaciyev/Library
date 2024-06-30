package core.project.library.application.model;

import core.project.library.domain.value_objects.ChangeOfOrder;
import core.project.library.domain.value_objects.CreditCard;
import core.project.library.domain.value_objects.PaidAmount;
import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrderDTO(@NotNull Integer countOfBooks,
                       @NotNull @Valid TotalPrice totalPrice,
                       @NotNull @Valid PaidAmount paidAmount,
                       @NotNull @Valid ChangeOfOrder changeOfOrder,
                       @NotNull @Valid CreditCard creditCard) {
}
