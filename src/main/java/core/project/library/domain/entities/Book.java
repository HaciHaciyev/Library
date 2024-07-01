package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book {
    private final UUID id;
    private final Title title;
    private Description description;
    private final ISBN isbn;
    private Price price;
    private QuantityOnHand quantityOnHand;
    private final Category category;
    private final Events events;
    private final /**@ManyToOne*/ Publisher publisher;
    private final /**@ManyToMany*/ Set<Author> authors;
    private final /**@ManyToMany*/ Set<Order> orders;

    public static Builder builder() {
        return new Builder();
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
                """, id.toString(), title.title(), description.description(),
                isbn.isbn(), category.toString(), price.price(), quantityOnHand.quantityOnHand(),
                events.creation_date().toString(), events.last_update_date().toString());
    }

    public static class Builder {
        private UUID id;
        private Title title;
        private Description description;
        private ISBN isbn;
        private Price price;
        private QuantityOnHand quantityOnHand;
        private Category category;
        private Events events;
        private /**@ManyToOne*/ Publisher publisher;
        private /**@ManyToMany*/ Set<Author> authors;

        private Builder() {}

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder title(final Title title) {
            this.title = title;
            return this;
        }

        public Builder description(final Description description) {
            this.description = description;
            return this;
        }

        public Builder isbn(final ISBN isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder price(final Price price) {
            this.price = price;
            return this;
        }

        public Builder quantityOnHand(final QuantityOnHand quantityOnHand) {
            this.quantityOnHand = quantityOnHand;
            return this;
        }

        public Builder category(final Category category) {
            this.category = category;
            return this;
        }

        public Builder events(final Events events) {
            this.events = events;
            return this;
        }

        public Builder publisher(final Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder authors(Set<Author> authors) {
            this.authors = authors;
            return this;
        }

        public final Book build() {
            validate();

            Book book = new Book(id, title, description, isbn, price, quantityOnHand,
                    category, events, publisher, Collections.unmodifiableSet(authors), new HashSet<>());

            publisher.addBook(book);
            authors.forEach(author -> author.addBook(book));
            return book;
        }

        private void validate() {
            if (Objects.isNull(id)) {
                throw new NullValueException("Book id can`t be null");
            }
            if (Objects.isNull(title)) {
                throw new NullValueException("Book title can`t be null");
            }
            if (Objects.isNull(description)) {
                throw new NullValueException("Book description can`t be null");
            }
            if (Objects.isNull(isbn)) {
                throw new NullValueException("Book isbn can`t be null");
            }
            if (Objects.isNull(price)) {
                throw new NullValueException("Book price can`t be null");
            }
            if (Objects.isNull(quantityOnHand)) {
                throw new NullValueException("Book quantityOnHand can`t be null");
            }
            if (Objects.isNull(category)) {
                throw new NullValueException("Book category can`t be null");
            }
            if (Objects.isNull(events)) {
                throw new NullValueException("Book events can`t be null");
            }
            if (Objects.isNull(publisher)) {
                throw new NullValueException("Book publisher can`t be null");
            }
            if (Objects.isNull(authors)) {
                throw new NullValueException("Book authors can`t be null");
            }

            if (price.price() < 0.0) {
                throw new NegativeValueException("Price can`t be negative");
            }
            if (quantityOnHand.quantityOnHand() < 0) {
                throw new NegativeValueException("Quantity can`t be negative");
            }
        }
    }
}
