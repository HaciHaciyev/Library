package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class Publisher {
    private final @NotNull UUID id;
    private final @NotNull PublisherName publisherName;
    private final @NotNull Address address;
    private final @NotNull Phone phone;
    private final @NotNull Email email;
    private final @NotNull Events events;
    private /**@OneToMany*/ Set<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;
        return Objects.equals(id, publisher.id) && Objects.equals(publisherName, publisher.publisherName)
                && Objects.equals(address, publisher.address) && Objects.equals(phone, publisher.phone)
                && Objects.equals(email, publisher.email) && Objects.equals(events, publisher.events);
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

    public void printBooks() {
        System.out.println(getBooks());
    }
}
