package core.project.library.domain.entities;

import core.project.library.application.model.BookModel;
import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book {
    private final @NotNull UUID id;
    private final @NotNull @Valid Title title;
    private final @NotNull @Valid Description description;
    private final @NotNull @Valid ISBN isbn;
    private final @NotNull BigDecimal price;
    private final @NotNull Integer quantityOnHand;
    private final @NotNull Category category;
    private final @NotNull @Valid Events events;
    private /**@ManyToOne*/ Publisher publisher;
    private /**@ManyToMany*/ Set<Author> authors;
    private /**@ManyToMany*/ Set<Order> orders;

    public static Book from(BookModel bookModel) {
        PublisherDTO publisherDTO = bookModel.publisher();
        Publisher publisher = Publisher.builder()
                .id(UUID.randomUUID())
                .publisherName(publisherDTO.publisherName())
                .address(publisherDTO.address())
                .phone(publisherDTO.phone())
                .email(publisherDTO.email())
                .events(new Events())
                .books(new HashSet<>())
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
                        .books(new HashSet<>())
                        .build()).collect(Collectors.toSet());


        return Book.builder()
                .id(UUID.randomUUID())
                .title(bookModel.title())
                .description(bookModel.description())
                .isbn(bookModel.isbn())
                .price(bookModel.price())
                .quantityOnHand(bookModel.quantityOnHand())
                .category(bookModel.category())
                .publisher(publisher)
                .authors(authors)
                .events(new Events())
                .orders(new HashSet<>())
                .build();
    }

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
}
