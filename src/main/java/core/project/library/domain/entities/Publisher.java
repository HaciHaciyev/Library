package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Publisher {
    private final UUID id;
    private final PublisherName publisherName;
    private final Address address;
    private final Phone phone;
    private final Email email;
    private final Events events;
    private final /**@OneToMany*/ Set<Book> books;

    void addBook(Book book) {
        this.books.add(book);
    }

    public Set<Book> getBooks() {
        return new HashSet<>(books);
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private UUID id;
        private PublisherName publisherName;
        private Address address;
        private Phone phone;
        private Email email;
        private Events events;

        private Builder() {}

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder publisherName(final PublisherName publisherName) {
            this.publisherName = publisherName;
            return this;
        }

        public Builder address(final Address address) {
            this.address = address;
            return this;
        }

        public Builder phone(final Phone phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(final Email email) {
            this.email = email;
            return this;
        }

        public Builder events(final Events events) {
            this.events = events;
            return this;
        }

        public final Publisher build() {
            validate();

            return new Publisher(id, publisherName, address,
                    phone, email, events, new HashSet<>());
        }

        private void validate() {
            if (id == null) {
                throw new IllegalArgumentException("id can't be null");
            }
            if (publisherName == null) {
                throw new IllegalArgumentException("publisherName can't be null");
            }
            if (address == null) {
                throw new IllegalArgumentException("address can't be null");
            }
            if (phone == null) {
                throw new IllegalArgumentException("phone can't be null");
            }
            if (email == null) {
                throw new IllegalArgumentException("email can't be null");
            }
            if (events == null) {
                throw new IllegalArgumentException("events can't be null");
            }
        }
    }
}
