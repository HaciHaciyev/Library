package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.TotalPrice;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Order {
    private @NotNull UUID id;
    private @NotNull Integer countOfBooks;
    private @NotNull TotalPrice totalPrice;
    private @NotNull Events events;
    private /**@ManyToOne*/ Customer customer;
    private /**@ManyToMany*/ Set<Book> books;

    public void addCustomer(Customer customer) {
        this.setCustomer(customer);
        customer.getOrders().add(this);
    }

    public void removeCustomer(Customer customer) {
        this.setCustomer(null);
        customer.getOrders().remove(this);
    }

    public void addBook(Book book) {
        this.books.add(book);
        book.getOrders().add(this);
    }

    public void removeBook(Book book) {
        this.books.remove(book);
        book.getOrders().remove(this);
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

    public void printCustomer() {
        System.out.println(getCustomer());
    }

    public void printBooks() {
        System.out.println(getBooks());
    }
}
