package core.project.library.domain.entities;

import core.project.library.application.model.CustomerModel;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {
    private final @NotNull UUID id;
    private final @NotNull @Valid FirstName firstName;
    private final @NotNull @Valid LastName lastName;
    private final @NotNull @Valid Password password;
    private final @NotNull @Valid Email email;
    private final @NotNull @Valid Address address;
    private final @NotNull @Valid Events events;
    private /**@OneToMany*/ Set<Order> orders;


    public static Customer from(CustomerModel model) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .firstName(model.firstName())
                .lastName(model.lastName())
                .password(model.password())
                .email(model.email())
                .address(model.address())
                .events(new Events())
                .orders(new HashSet<>())
                .build();
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setCustomer(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) &&
                Objects.equals(lastName, customer.lastName) && Objects.equals(password, customer.password) &&
                Objects.equals(email, customer.email) && Objects.equals(events, customer.events);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(firstName);
        result = 31 * result + Objects.hashCode(lastName);
        result = 31 * result + Objects.hashCode(password);
        result = 31 * result + Objects.hashCode(email);
        result = 31 * result + Objects.hashCode(events);
        return result;
    }

    @Override
    public String toString() {
        return String.format("""
                id = %s,
                first_name = %s,
                last_name = %s,
                password = %s,
                email = %s,
                creation_date = %s,
                last_modified_date = %s
                """, id.toString(), firstName.firstName(), lastName.lastName(),
                password.password(), email.email(),
                events.creation_date().toString(), events.last_update_date().toString());
    }
}
