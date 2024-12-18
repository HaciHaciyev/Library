package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.FirstName;
import core.project.library.domain.value_objects.LastName;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class Author {
    private final @NotNull UUID id;
    private final @NotNull FirstName firstName;
    private final @NotNull LastName lastName;
    private final @NotNull Email email;
    private final @NotNull Address address;
    private final @NotNull Events events;
    private /**@ManyToMany*/ Set<Book> books;

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

    public void printBooks() {
        System.out.println(getBooks());
    }
}
