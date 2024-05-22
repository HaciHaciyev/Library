package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Author {
    private final UUID id;
    private final FirstName firstName;
    private final LastName lastName;
    private final Email email;
    private final Address address;
    private final Events events;
    private final /**@ManyToMany*/ Set<Book> books;

    protected void addBook(Book book) {
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

        Author author = (Author) o;
        return Objects.equals(id, author.id) && Objects.equals(firstName, author.firstName) &&
                Objects.equals(lastName, author.lastName) && Objects.equals(email, author.email) &&
                Objects.equals(address, author.address) && Objects.equals(events, author.events);
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

    public static class Builder {
        private UUID id;
        private FirstName firstName;
        private LastName lastName;
        private Email email;
        private Address address;
        private Events events;

        private Builder() {}

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder firstName(final FirstName firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(final LastName lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(final Email email) {
            this.email = email;
            return this;
        }

        public Builder address(final Address address) {
            this.address = address;
            return this;
        }

        public Builder events(final Events events) {
            this.events = events;
            return this;
        }

        public Author build() {
            validateToNullAndBlank(new Object[]{id, firstName, lastName,
                    email, address, events});

            return new Author(id, firstName, lastName,
                    email, address, events, new HashSet<>());
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
