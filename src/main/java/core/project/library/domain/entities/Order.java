package core.project.library.domain.entities;

import core.project.library.domain.value_objects.ChangeOfOrder;
import core.project.library.domain.value_objects.CreditCard;
import core.project.library.domain.value_objects.PaidAmount;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.exceptions.InsufficientPaymentException;
import core.project.library.infrastructure.exceptions.NegativeValueException;
import core.project.library.infrastructure.exceptions.NullValueException;
import core.project.library.infrastructure.exceptions.QuantityOnHandException;
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
    private final LocalDateTime creationDate;
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
                Objects.equals(creationDate, order.creationDate) &&
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
        result = 31 * result + Objects.hashCode(creationDate);
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
                changeOfOrder.changeOfOrder(), creationDate.toString()
        );
    }

    public static class Builder {
        private UUID id;
        private PaidAmount paidAmount;
        private CreditCard creditCard;
        private LocalDateTime creationDate;
        private /**@ManyToOne*/ Customer customer;
        private /**@ManyToMany*/ Map<Book, Integer> books;

        private Builder() {}

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder paidAmount(final PaidAmount paidAmount) {
            this.paidAmount = paidAmount;
            return this;
        }

        public Builder creditCard(final CreditCard creditCard) {
            this.creditCard = creditCard;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
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
            Integer countOfBooks = calculateCountOfBooks(books);
            TotalPrice totalPrice = calculateTotalPrice(books);
            ChangeOfOrder changeOfOrder = calculateChange(totalPrice, paidAmount);

            validate(countOfBooks, totalPrice, changeOfOrder);
            validateQuantityOnHandOfBooksAndChangeIt(books);

            Order order = new Order(id, countOfBooks, totalPrice, paidAmount, changeOfOrder,
                    creditCard, creationDate, customer, Collections.unmodifiableMap(books));

            customer.addOrder(order);
            books.forEach((book, _) -> book.addOrder(order));
            return order;
        }

        private void validate(Integer countOfBooks, TotalPrice totalPrice, ChangeOfOrder changeOfOrder) {
            if (Objects.isNull(id)) {
                throw new NullValueException("Order id can`t be null");
            }
            if (Objects.isNull(countOfBooks)) {
                throw new NullValueException("Order countOfBooks can`t be null");
            }
            if (Objects.isNull(totalPrice)) {
                throw new NullValueException("Order totalPrice can`t be null");
            }
            if (Objects.isNull(paidAmount)) {
                throw new NullValueException("Order paidAmount can`t be null");
            }
            if (Objects.isNull(changeOfOrder)) {
                throw new NullValueException("Order changeOfOrder can`t be null");
            }
            if (Objects.isNull(creditCard)) {
                throw new NullValueException("Order creditCard can`t be null");
            }
            if (Objects.isNull(creationDate)) {
                throw new NullValueException("Order creationDate can`t be null");
            }
            if (Objects.isNull(customer)) {
                throw new NullValueException("Order customer can`t be null");
            }
            if (Objects.isNull(books)) {
                throw new NullValueException("Order books can`t be null");
            }

            boolean isPaidAmountEnough = paidAmount.paidAmount() > totalPrice.totalPrice();

            if (countOfBooks <= 0) {
                throw new NegativeValueException("Count of books can`t be negative or zero");
            }
            if (totalPrice.totalPrice() < 0.0) {
                throw new NegativeValueException("Total price can`t be negative");
            }
            if (paidAmount.paidAmount() < 0.0) {
                throw new NegativeValueException("Paid amount can`t be negative or smaller than total price in order");
            }
            if (!isPaidAmountEnough) {
                throw new InsufficientPaymentException("The paid amount is not enough to complete the order");
            }
            if (books.isEmpty()) {
                throw new IllegalArgumentException("Books can`t be empty");
            }
        }

        private void validateQuantityOnHandOfBooksAndChangeIt(Map<Book, Integer> books) {
            for (Map.Entry<Book, Integer> pair : books.entrySet()) {
                Book book = pair.getKey();
                int requiredQuantityForOneCopyOfBook = pair.getValue();
                int existedQuantityOnHand = book.getQuantityOnHand().quantityOnHand();

                boolean isQuantityOnHandEnough = existedQuantityOnHand >= requiredQuantityForOneCopyOfBook;
                if (!isQuantityOnHandEnough) {
                    throw new QuantityOnHandException("We do not have enough books for this order.");
                }

                book.changeQuantityOnHand(existedQuantityOnHand - requiredQuantityForOneCopyOfBook);
            }
        }

        private Integer calculateCountOfBooks(Map<Book, Integer> books) {
            int countOfBooks = 0;
            for (Integer count : books.values()) {
                countOfBooks += count;
            }
            return countOfBooks;
        }

        private TotalPrice calculateTotalPrice(Map<Book, Integer> books) {
            double totalPrice = 0.0;

            for (Map.Entry<Book, Integer> pair : books.entrySet()) {
                int countOfBookCopies = pair.getValue();
                double priceOfBookInOneCopy = pair.getKey().getPrice().price();
                double priceOfAllCopyOfBook = priceOfBookInOneCopy * countOfBookCopies;

                totalPrice += priceOfAllCopyOfBook;
            }

            return new TotalPrice(totalPrice);
        }

        private ChangeOfOrder calculateChange(TotalPrice totalPrice, PaidAmount paidAmount) {
            return new ChangeOfOrder(paidAmount.paidAmount() - totalPrice.totalPrice());
        }
    }
}
