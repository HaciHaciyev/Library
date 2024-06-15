package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private final UUID id;
    private final Integer countOfBooks;
    private final TotalPrice totalPrice;
    private final Events events;
    private final /**@ManyToOne*/ Customer customer;
    private final /**@ManyToMany*/Set<Book> books;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        Set<UUID> ourBooks = books.stream().map(Book::getId).collect(Collectors.toSet());
        Set<UUID> theirBooks = order.books.stream().map(Book::getId).collect(Collectors.toSet());

        return Objects.equals(id, order.id) &&
                Objects.equals(countOfBooks, order.countOfBooks) &&
                Objects.equals(totalPrice, order.totalPrice) &&
                Objects.equals(events, order.events) &&
                Objects.equals(customer, order.customer) &&
                Objects.equals(ourBooks, theirBooks);
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
        private /**@ManyToOne*/ Customer customer;
        private /**@ManyToMany*/ Set<Book> books;

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

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder books(Set<Book> books) {
            this.books = books;
            return this;
        }

        public final Order build() {
            validate();

            Order order = new Order(id, countOfBooks, totalPrice,
                    events, customer, Collections.unmodifiableSet(books));

            customer.addOrder(order);
            books.forEach(book -> book.addOrder(order));
            return order;
        }

        private void validate() {
            Objects.requireNonNull(countOfBooks, "countOfBooks can't be null");
            Objects.requireNonNull(totalPrice, "totalPrice can't be null");
            Objects.requireNonNull(events, "events can't be null");
            Objects.requireNonNull(customer, "customer can't be null");
            Objects.requireNonNull(books, "books can't be null");

            if (countOfBooks < 0) {
                throw new IllegalArgumentException("Count of books can't be negative");
            }
            if (totalPrice.totalPrice().doubleValue() < 0) {
                throw new IllegalArgumentException("Total price can't be negative");
            }
            if (books.isEmpty()) {
                throw new IllegalArgumentException("Books can't be empty");
            }
        }
    }
}
