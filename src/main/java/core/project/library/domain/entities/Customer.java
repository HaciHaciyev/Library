package core.project.library.domain.entities;

import core.project.library.application.model.CustomerModel;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {
    private final UUID id;
    private final FirstName firstName;
    private final LastName lastName;
    private final Password password;
    private final Email email;
    private final Address address;
    private final Events events;
    private /**@OneToMany*/ Set<Order> orders;

    public final void addOrder(Order order) {
        Objects.requireNonNull(order);
        this.orders.add(order);
        order.setCustomer(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Customer from(CustomerModel model) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .firstName(model.firstName())
                .lastName(model.lastName())
                .password(model.password())
                .email(model.email())
                .address(model.address())
                .events(new Events())
                .build();
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

    public static class Builder {
        private UUID id;
        private FirstName firstName;
        private LastName lastName;
        private Password password;
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

        public Builder password(final Password password) {
            this.password = password;
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

        public Customer build() {
            validateToNullAndBlank(new Object[]{id, firstName, lastName,
                    password, email, address, events});

            return new Customer(id, firstName, lastName, password,
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
