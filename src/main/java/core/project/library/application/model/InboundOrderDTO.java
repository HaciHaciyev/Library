package core.project.library.application.model;

import core.project.library.domain.value_objects.CreditCard;
import core.project.library.domain.value_objects.PaidAmount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record InboundOrderDTO(@NotNull @Valid PaidAmount paidAmount,
                              @NotNull @Valid CreditCard creditCard,
                              @NotNull UUID customerId,
                              @NotNull List<UUID> booksId) {
}
