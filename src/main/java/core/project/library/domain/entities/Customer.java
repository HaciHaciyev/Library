package core.project.library.domain.entities;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Customer {
    private final UUID id;
    private final FirstName firstName;
    private final LastName lastName;
    private final Password password;
    private final Email email;
    private final Address address;
    private final Events events;
    private final /**@OneToMany*/ Set<Order> orders;

    private Customer(UUID id, FirstName firstName, LastName lastName, Password password,
                    Email email, Address address, Events events, Set<Order> orders) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(password);
        Objects.requireNonNull(email);
        Objects.requireNonNull(address);
        Objects.requireNonNull(events);
        Objects.requireNonNull(orders);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.events = events;
        this.orders = orders;
    }

    public static Customer create(UUID id, FirstName firstName, LastName lastName, Password password,
                                  Email email, Address address, Events events) {
        return new Customer(id, firstName, lastName, password, email, address, events, new HashSet<>());
    }

    void addOrder(Order order) {
        this.orders.add(order);
    }

    public Set<Order> getOrders() {
        return new HashSet<>(orders);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        Set<UUID> ourOrders = orders.stream().map(Order::getId).collect(Collectors.toSet());
        Set<UUID> theirOrders = customer.orders.stream().map(Order::getId).collect(Collectors.toSet());

        return Objects.equals(id, customer.id) &&
                Objects.equals(firstName, customer.firstName) &&
                Objects.equals(lastName, customer.lastName) &&
                Objects.equals(password, customer.password) &&
                Objects.equals(email, customer.email) &&
                Objects.equals(events, customer.events) &&
                Objects.equals(ourOrders, theirOrders);
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
                Customer {
                id = %s,
                first_name = %s,
                last_name = %s,
                password = %s,
                email = %s,
                creation_date = %s,
                last_modified_date = %s
                }
                """, id, firstName.firstName(), lastName.lastName(),
                password.password(), email.email(),
                events.creation_date().toString(), events.last_update_date().toString());
    }
}
