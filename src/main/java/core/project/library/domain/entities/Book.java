package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Book {
    private final UUID id;
    private final Title title;
    private Description description;
    private final ISBN isbn;
    private Price price;
    private QuantityOnHand quantityOnHand;
    private final Category category;
    private final Events events;
    private Boolean withdrawnFromSale;
    private final /**@ManyToOne*/ Publisher publisher;
    private final /**@ManyToMany*/ Set<Author> authors;
    private final /**@ManyToMany*/ Set<Order> orders;

    private Book(UUID id, Title title, Description description, ISBN isbn,
                Price price, QuantityOnHand quantityOnHand, Category category, Events events,
                Boolean withdrawnFromSale, Publisher publisher, Set<Author> authors, Set<Order> orders) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(isbn);
        Objects.requireNonNull(price);
        Objects.requireNonNull(quantityOnHand);
        Objects.requireNonNull(category);
        Objects.requireNonNull(events);
        Objects.requireNonNull(withdrawnFromSale);
        Objects.requireNonNull(publisher);
        Objects.requireNonNull(authors);
        Objects.requireNonNull(orders);

        this.id = id;
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.price = price;
        this.quantityOnHand = quantityOnHand;
        this.category = category;
        this.events = events;
        this.withdrawnFromSale = withdrawnFromSale;
        this.publisher = publisher;
        this.authors = authors;
        this.orders = orders;
    }

    /**
     * upon creation book adds itself to publisher and authors
     */
    public static Book create(UUID id, Title title, Description description, ISBN isbn,
                              Price price, QuantityOnHand quantityOnHand, Category category, Events events,
                              Boolean withdrawnFromSale, Publisher publisher, Set<Author> authors) {
        Book book = new Book(
                id,
                title,
                description,
                isbn,
                price,
                quantityOnHand,
                category,
                events,
                withdrawnFromSale,
                publisher,
                authors,
                new HashSet<>()
        );

        publisher.addBook(book);
        authors.forEach(a -> a.addBook(book));
        return book;
    }

    void addOrder(Order order) {
        this.orders.add(order);
    }

    public Set<Order> getOrders() {
        return new HashSet<>(orders);
    }

    public Description getDescription() {
        return new Description(description.description());
    }

    public Price getPrice() {
        return new Price(price.price());
    }

    public QuantityOnHand getQuantityOnHand() {
        return new QuantityOnHand(quantityOnHand.quantityOnHand());
    }

    public void changeDescription(String description) {
        this.description = new Description(description);
    }

    public void changePrice(Double price) {
        this.price = new Price(price);
    }

    public void changeQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = new QuantityOnHand(quantityOnHand);
    }

    public boolean isItOnSale() {
        return !withdrawnFromSale;
    }

    public void withdrawnFromSale() {
        this.withdrawnFromSale = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        Set<UUID> ourAuthors = authors.stream().map(Author::getId).collect(Collectors.toSet());
        Set<UUID> theirAuthors = book.authors.stream().map(Author::getId).collect(Collectors.toSet());

        Set<UUID> ourOrders = orders.stream().map(Order::getId).collect(Collectors.toSet());
        Set<UUID> theirOrders = book.orders.stream().map(Order::getId).collect(Collectors.toSet());

        return Objects.equals(id, book.id) &&
                Objects.equals(title, book.title) &&
                Objects.equals(description, book.description) &&
                Objects.equals(isbn, book.isbn) &&
                Objects.equals(price, book.price) &&
                Objects.equals(quantityOnHand, book.quantityOnHand) &&
                Objects.equals(events, book.events) &&
                category == book.category &&
                Objects.equals(publisher.getId(), book.publisher.getId()) &&
                Objects.equals(ourAuthors, theirAuthors) &&
                Objects.equals(ourOrders, theirOrders);
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
                """, id, title.title(), description.description(),
                isbn.isbn(), category, price.price(), quantityOnHand.quantityOnHand(),
                events.creation_date().toString(), events.last_update_date().toString());
    }
}
