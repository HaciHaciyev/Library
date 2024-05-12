package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private final UUID id;
    private final Integer countOfBooks;
    private final TotalPrice totalPrice;
    private final Events events;
    private /**@ManyToOne*/ @Setter(AccessLevel.PROTECTED) Customer customer;
    private /**@ManyToMany*/ Set<Book> books;

    public static Builder builder() {
        return new Builder();
    }

    private static Order factory(UUID id, Integer countOfBooks,
                                 TotalPrice totalPrice, Events events,
                                 Customer customer, Set<Book> books) {
        validateToNullAndBlank(new Object[]{id, countOfBooks,
                totalPrice, events});

        return new Order(id, countOfBooks, totalPrice,
                events, customer, books);
    }

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

    public static class Builder {
        private UUID id;
        private Integer countOfBooks;
        private TotalPrice totalPrice;
        private Events events;
        private Customer customer;
        private Set<Book> books;

        private Builder() {}

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder countOfBooks(final Integer countOfBooks) {
            this.countOfBooks = countOfBooks;
            return this;
        }

        public Builder totalPrice(final TotalPrice totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder events(final Events events) {
            this.events = events;
            return this;
        }

        public Builder customer(final Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder books(final Set<Book> books) {
            this.books = books;
            return this;
        }

        public Order build() {
            return factory(this.id, this.countOfBooks,
                    this.totalPrice, this.events,
                    this.customer, this.books);
        }
    }

    private static void validateToNullAndBlank(Object[] o) {
        for (Object object : o) {
            Objects.requireNonNull(object);
            if (object instanceof String) {
                if (((String) object).isBlank()) {
                    throw new IllegalArgumentException("String should`t be blank.");
                }
            }
        }
    }
}
