package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class Book {
    private @NotNull UUID id;
    private @NotNull Title title;
    private @NotNull Description description;
    private @NotNull ISBN isbn;
    private @NotNull BigDecimal price;
    private @NotNull Integer quantityOnHand;
    private @NotNull Category category;
    private @NotNull Events events;
    private /**@ManyToOne*/ Publisher publisher;
    private /**@ManyToMany*/ Set<Author> authors;
    private /**@ManyToMany*/ Set<Order> orders;

    public void addPublisher(Publisher publisher) {
        this.setPublisher(publisher);
        publisher.getBooks().add(this);
    }

    public void removePublisher(Publisher publisher) {
        this.setPublisher(null);
        publisher.getBooks().remove(this);
    }

    public void addAuthor(Author author) {
        this.getAuthors().add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        order.getBooks().add(this);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.getBooks().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) &&
                Objects.equals(description, book.description) && Objects.equals(isbn, book.isbn) &&
                Objects.equals(price, book.price) && Objects.equals(quantityOnHand, book.quantityOnHand) &&
                Objects.equals(events, book.events) && category == book.category;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(isbn);
        result = 31 * result + Objects.hashCode(price);
        result = 31 * result + Objects.hashCode(quantityOnHand);
        result = 31 * result + Objects.hashCode(events);
        result = 31 * result + Objects.hashCode(category);
        return result;
    }

    @Override
    public String toString() {
        return String.format("""
                Book {
                id = %s,
                title = %s,
                description = %s,
                isbn = %s,
                category = %s,
                price = %f,
                quantity_on_hand = %d,
                creation_date = %s,
                last_modified_date = %s
                }
                """, id.toString(), title.title(), description.description(),
                isbn.isbn(), category.toString(), price, quantityOnHand,
                events.creation_date().toString(), events.last_update_date().toString());
    }

    public void printPublisher() {
        System.out.println(getPublisher());
    }

    public void printAuthors() {
        System.out.println(getAuthors());
    }

    public void printOrders() {
        System.out.println(getOrders());
    }

    public static Book compound(Book book, Publisher publisher,
                                List<Optional<Author>> authors, List<Optional<Order>> orders) {
        Set<Author> authorSet = new HashSet<>();
        for (Optional<Author> author : authors) {
            authorSet.add(author.orElseThrow());
        }
        Set<Order> orderSet = new HashSet<>();
        for (Optional<Order> order : orders) {
            orderSet.add(order.orElseThrow());
        }
        return Book.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .quantityOnHand(book.getQuantityOnHand())
                .category(book.getCategory())
                .events(book.getEvents())
                .publisher(publisher)
                .authors(authorSet)
                .orders(orderSet)
                .build();
    }
}
