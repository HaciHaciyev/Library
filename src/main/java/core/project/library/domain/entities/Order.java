package core.project.library.domain.entities;

import core.project.library.domain.value_objects.ChangeOfOrder;
import core.project.library.domain.value_objects.CreditCard;
import core.project.library.domain.value_objects.PaidAmount;
import core.project.library.domain.value_objects.TotalPrice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private final UUID id;
    private final Integer countOfBooks;
    private final TotalPrice totalPrice;
    private final PaidAmount paidAmount;
    private final ChangeOfOrder changeOfOrder;
    private final CreditCard creditCard;
    private final LocalDateTime creationTime;
    private final /**@ManyToOne*/ Customer customer;
    private final /**@ManyToMany*/ Map<Book, Integer> books;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        List<UUID> ourBooks = books.keySet().stream().map(Book::getId).toList();
        List<UUID> theirBooks = order.books.keySet().stream().map(Book::getId).toList();

        return Objects.equals(id, order.id) &&
                Objects.equals(countOfBooks, order.countOfBooks) &&
                Objects.equals(totalPrice, order.totalPrice) &&
                Objects.equals(paidAmount, order.paidAmount) &&
                Objects.equals(changeOfOrder, order.changeOfOrder) &&
                Objects.equals(creditCard, order.creditCard) &&
                Objects.equals(creationTime, order.creationTime) &&
                Objects.equals(customer, order.customer) &&
                Objects.equals(ourBooks, theirBooks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(countOfBooks);
        result = 31 * result + Objects.hashCode(totalPrice);
        result = 31 * result + Objects.hashCode(paidAmount);
        result = 31 * result + Objects.hashCode(changeOfOrder);
        result = 31 * result + Objects.hashCode(creditCard);
        result = 31 * result + Objects.hashCode(creationTime);
        result = 31 * result + Objects.hashCode(customer);
        return result;
    }

    @Override
    public String toString() {
        return String.format("""
                Order {
                id = %s,
                count_of_books = %d,
                total_price = %f,
                paid_amount = %f,
                change_of_order = %f,
                creation_date = %s,
                }
                """,
                id.toString(), countOfBooks,
                totalPrice.totalPrice(), paidAmount.paidAmount(),
                changeOfOrder.changeOfOrder(), creationTime.toString()
        );
    }

    public static class Builder {
        private UUID id;
        private Integer countOfBooks;
        private PaidAmount paidAmount;
        private CreditCard creditCard;
        private LocalDateTime creationTime;
        private /**@ManyToOne*/ Customer customer;
        private /**@ManyToMany*/ Map<Book, Integer> books;

        private Builder() {}

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder countOfBooks(final Integer countOfBooks) {
            this.countOfBooks = countOfBooks;
            return this;
        }

        public Builder setPaidAmount(final PaidAmount paidAmount) {
            this.paidAmount = paidAmount;
            return this;
        }

        public Builder setCreditCard(final CreditCard creditCard) {
            this.creditCard = creditCard;
            return this;
        }

        public Builder setCreationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder books(Map<Book, Integer> books) {
            this.books = books;
            return this;
        }

        public final Order build() {
            TotalPrice totalPrice = calculateTotalPrice(books);
            ChangeOfOrder changeOfOrder = calculateChange(totalPrice, paidAmount);

            validate(totalPrice, changeOfOrder);

            Order order = new Order(id, countOfBooks, totalPrice, paidAmount, changeOfOrder,
                    creditCard, creationTime, customer, Collections.unmodifiableMap(books));

            customer.addOrder(order);
            books.forEach((book, _) -> book.addOrder(order));
            return order;
        }

        private void validate(TotalPrice totalPrice, ChangeOfOrder changeOfOrder) {
            Objects.requireNonNull(countOfBooks, "countOfBooks can`t be null");
            Objects.requireNonNull(paidAmount, "paid amount can`t be null");
            Objects.requireNonNull(totalPrice, "totalPrice can`t be null");
            Objects.requireNonNull(changeOfOrder, "changeOfOrder can`t be null");
            Objects.requireNonNull(creditCard, "credit card can`t be null");
            Objects.requireNonNull(creationTime, "creation time can`t be null");
            Objects.requireNonNull(customer, "customer can`t be null");
            Objects.requireNonNull(books, "books can`t be null");

            boolean isPaidAmountEnough = paidAmount.paidAmount() > totalPrice.totalPrice();

            if (countOfBooks < 0) {
                throw new IllegalArgumentException("Count of books can`t be negative");
            }
            if (paidAmount.paidAmount() < 0 && !isPaidAmountEnough) {
                throw new IllegalArgumentException("Paid amount can`t be negative or smaller than total price in order");
            }
            if (books.isEmpty()) {
                throw new IllegalArgumentException("Books can`t be empty");
            }
        }

        private TotalPrice calculateTotalPrice(Map<Book, Integer> books) {
            double totalPrice = 0.0;

            for (Map.Entry<Book, Integer> pair : books.entrySet()) {
                double priceOfBookInOneCopy = pair.getKey().getPrice().price();
                double currentPairPrice = priceOfBookInOneCopy * pair.getValue();

                totalPrice += currentPairPrice;
            }

            return new TotalPrice(totalPrice);
        }

        private ChangeOfOrder calculateChange(TotalPrice totalPrice, PaidAmount paidAmount) {
            return new ChangeOfOrder(paidAmount.paidAmount() - totalPrice.totalPrice());
        }
    }
}
