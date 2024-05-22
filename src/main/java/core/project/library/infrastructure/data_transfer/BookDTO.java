package core.project.library.infrastructure.data_transfer;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Builder
public record BookDTO(UUID id, UUID publisherId, Title title, Description description,
                      ISBN isbn, BigDecimal price, Integer quantityOnHand, Category category,
                      Events events) {

    public BookDTO {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(publisherId, " publisherId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(isbn, "isbn must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(quantityOnHand, "quantityOnHand must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(events, "events must not be null");

        validate(price, quantityOnHand);
    }

    private void validate(BigDecimal p, Integer quantity) {

        if (p.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new IllegalArgumentException("The price cannot be below zero");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("The quantity cannot be below zero");
        }
    }
}
