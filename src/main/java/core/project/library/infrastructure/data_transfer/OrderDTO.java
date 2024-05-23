package core.project.library.infrastructure.data_transfer;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;

import java.util.Objects;
import java.util.UUID;

public record OrderDTO(UUID id, Integer countOfBooks, TotalPrice totalPrice, Events events) {

    public OrderDTO {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(countOfBooks, "count of book cannot be null");
        Objects.requireNonNull(totalPrice, "totalPrice cannot be null");
        Objects.requireNonNull(events, "events cannot be null");
        validate(countOfBooks, totalPrice);
    }

    private void validate(Integer countOfBooks, TotalPrice totalPrice) {
        if (countOfBooks < 1) {
            throw new IllegalArgumentException("countOfBooks is less than 1");
        }
        if (totalPrice.totalPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("totalPrice is less than 0");
        }
    }

}
