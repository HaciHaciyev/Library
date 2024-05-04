package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class Order {
    private final @NotNull UUID id;
    private final @NotNull Integer countOfBooks;
    private final @NotNull TotalPrice totalPrice;
    private final @NotNull Events events;
    private /**@ManyToOne*/ @Setter(AccessLevel.PROTECTED) Customer customer;
    private /**@ManyToMany*/ Set<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(countOfBooks, order.countOfBooks) &&
                Objects.equals(totalPrice, order.totalPrice) && Objects.equals(events, order.events) &&
                Objects.equals(customer, order.customer);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(countOfBooks);
        result = 31 * result + Objects.hashCode(totalPrice);
        result = 31 * result + Objects.hashCode(events);
        result = 31 * result + Objects.hashCode(customer);
        return result;
    }

    @Override
    public String toString() {
        return String.format("""
                Order {
                id = %s,
                count_of_books = %d,
                total_price = %f,
                creation_date = %s,
                last_modified_date = %s
                }
                """, id.toString(), countOfBooks, totalPrice.totalPrice(),
                events.creation_date().toString(), events.last_update_date().toString());
    }

    public void printCustomer() {
        System.out.println(getCustomer());
    }

    public void printBooks() {
        System.out.println(getBooks());
    }
}
