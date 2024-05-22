package core.project.library.domain.entities;

import core.project.library.application.model.BookModel;
import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book {
    private final UUID id;
    private final Title title;
    private final Description description;
    private final ISBN isbn;
    private final BigDecimal price;
    private final Integer quantityOnHand;
    private final Category category;
    private final Events events;
    private final /**@ManyToOne*/ Publisher publisher;
    private final /**@ManyToMany*/ Set<Author> authors;
    private final /**@ManyToMany*/ Set<Order> orders;

    protected void addOrder(Order order) {
        this.orders.add(order);
    }

    public Set<Order> getOrders() {
        return new HashSet<>(orders);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Book from(BookModel bookModel) {
        Objects.requireNonNull(bookModel);

        PublisherDTO publisherDTO = bookModel.publisher();
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(publisherDTO.publisherName())
                .address(publisherDTO.address())
                .phone(publisherDTO.phone())
                .email(publisherDTO.email())
                .events(new Events())
                .build();

        Set<Author> authors = bookModel.authors()
                .stream()
                .map(authorDTO -> Author.builder()
                        .id(UUID.randomUUID())
                        .firstName(authorDTO.firstName())
                        .lastName(authorDTO.lastName())
                        .email(authorDTO.email())
                        .address(authorDTO.address())
                        .events(new Events())
                        .build()).collect(Collectors.toSet());

        return Book.builder()
                .id(UUID.randomUUID())
                .title(bookModel.title())
                .description(bookModel.description())
                .isbn(bookModel.isbn())
                .price(bookModel.price())
                .quantityOnHand(bookModel.quantityOnHand())
                .category(bookModel.category())
                .events(new Events())
                .publisher(publisher)
                .authors(authors)
                .build();
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

    public static class Builder {
        private UUID id;
        private Title title;
        private Description description;
        private ISBN isbn;
        private BigDecimal price;
        private Integer quantityOnHand;
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

        public Builder price(final BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder quantityOnHand(final Integer quantityOnHand) {
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
            validateToNullAndBlank(new Object[]{id, title, description, isbn,
                    price, quantityOnHand, category, events, publisher, authors});
            if (authors.isEmpty()) {
                throw new IllegalArgumentException("Authors can`t be empty.");
            }

            Book book = new Book(id, title, description, isbn, price, quantityOnHand,
                    category, events, publisher, Collections.unmodifiableSet(authors), new HashSet<>());
            publisher.addBook(book);
            authors.forEach(author -> author.addBook(book));
            return book;
        }
    }

    private static void validateToNullAndBlank(Object[] o) {
        for (Object object : o) {
            Objects.requireNonNull(object);
            if (object instanceof String string && string.isBlank()) {
                throw new IllegalArgumentException("String should`t be blank.");
            }
        }
    }
}
