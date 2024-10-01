package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Publisher {
    private final UUID id;
    private final PublisherName publisherName;
    private final Address address;
    private final Phone phone;
    private final Email email;
    private final Events events;
    private final /**@OneToMany*/
            Set<Book> books;

    private Publisher(UUID id, PublisherName publisherName, Address address,
                      Phone phone, Email email, Events events,
                      Set<Book> books) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(publisherName);
        Objects.requireNonNull(address);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(email);
        Objects.requireNonNull(events);
        Objects.requireNonNull(books);

        this.id = id;
        this.publisherName = publisherName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.events = events;
        this.books = books;
    }

    public static Publisher create(UUID id, PublisherName publisherName, Address address,
                                   Phone phone, Email email, Events events) {
        return new Publisher(id, publisherName, address,
                phone, email, events, new HashSet<>());
    }

    void addBook(Book book) {
        this.books.add(book);
    }

    public Set<Book> getBooks() {
        return new HashSet<>(books);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;

        Set<UUID> ourBooks = books.stream().map(Book::getId).collect(Collectors.toSet());
        Set<UUID> theirBooks = publisher.books.stream().map(Book::getId).collect(Collectors.toSet());

        return Objects.equals(id, publisher.id) &&
                Objects.equals(publisherName, publisher.publisherName) &&
                Objects.equals(address, publisher.address) &&
                Objects.equals(phone, publisher.phone) &&
                Objects.equals(email, publisher.email) &&
                Objects.equals(events, publisher.events) &&
                Objects.equals(ourBooks, theirBooks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(publisherName);
        result = 31 * result + Objects.hashCode(address);
        result = 31 * result + Objects.hashCode(phone);
        result = 31 * result + Objects.hashCode(email);
        result = 31 * result + Objects.hashCode(events);
        return result;
    }

    @Override
    public String toString() {
        return String.format("""
                        Publisher {
                        id = %s,
                        publisher_name = %s,
                        state = %s,
                        city = %s,
                        street = %s,
                        home = %s,
                        email = %s,
                        creation_date = %s,
                        last_modified_date = %s
                        }
                        """, id.toString(), publisherName.publisherName(),
                address.state(), address.city(), address.street(), address.home(),
                email.email(), events.creation_date().toString(), events.last_update_date().toString());
    }
}
