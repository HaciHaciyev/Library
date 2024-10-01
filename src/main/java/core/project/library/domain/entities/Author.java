package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Author {
    private final UUID id;
    private final FirstName firstName;
    private final LastName lastName;
    private final Email email;
    private final Address address;
    private final Events events;
    private final Set<Book> books;

    private Author(UUID id, FirstName firstName, LastName lastName,
                  Email email, Address address, Events events,
                  Set<Book> books) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(email);
        Objects.requireNonNull(address);
        Objects.requireNonNull(events);
        Objects.requireNonNull(books);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.events = events;
        this.books = books;
    }

    public static Author create(UUID id, FirstName firstName, LastName lastName,
                                Email email, Address address, Events events) {
        return new Author(id, firstName, lastName, email, address, events, new HashSet<>());
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

        Author author = (Author) o;

        Set<UUID> ourBooks = books.stream().map(Book::getId).collect(Collectors.toSet());
        Set<UUID> theirBooks = author.books.stream().map(Book::getId).collect(Collectors.toSet());

        return Objects.equals(id, author.id) &&
                Objects.equals(firstName, author.firstName) &&
                Objects.equals(lastName, author.lastName) &&
                Objects.equals(email, author.email) &&
                Objects.equals(address, author.address) &&
                Objects.equals(events, author.events) &&
                Objects.equals(ourBooks, theirBooks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(firstName);
        result = 31 * result + Objects.hashCode(lastName);
        result = 31 * result + Objects.hashCode(email);
        result = 31 * result + Objects.hashCode(address);
        result = 31 * result + Objects.hashCode(events);
        return result;
    }

    @Override
    public String toString() {
        return String.format("""
                        Author {
                        id = %s,
                        first_name = %s,
                        last_name = %s,
                        email = %s,
                        state = %s,
                        city = %s,
                        street = %s,
                        home = %s,
                        creation_date = %s,
                        last_modified_date = %s
                        }
                        """, id.toString(), firstName.firstName(), lastName.lastName(),
                email.email(), address.state(), address.city(), address.street(), address.home(),
                events.creation_date().toString(), events.last_update_date().toString());
    }
}
