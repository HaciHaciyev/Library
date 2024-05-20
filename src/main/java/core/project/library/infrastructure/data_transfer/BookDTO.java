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
        validate(new Object[]{id, price, title, description,
                isbn, price, quantityOnHand, category, events});
    }

    private void validate(Object[] o) {
        for (Object object : o) {
            Objects.requireNonNull(object);
            if (object instanceof BigDecimal bigDecimal
                    && bigDecimal.compareTo(BigDecimal.valueOf(0)) < 0) {
                    throw new IllegalArgumentException("The price cannot be below zero.");
            }
            if (object instanceof Integer integer && integer < 0) {
                    throw new IllegalArgumentException("The quantity on hand cannot be below zero.");
            }
        }
    }
}
