package core.project.library.domain.entities;

import core.project.library.domain.value_objects.ChangeOfOrder;
import core.project.library.domain.value_objects.CreditCard;
import core.project.library.domain.value_objects.PaidAmount;
import core.project.library.domain.value_objects.TotalPrice;
import core.project.library.infrastructure.exceptions.InsufficientPaymentException;
import core.project.library.infrastructure.exceptions.NegativeValueException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Getter
public class Order {
    private final UUID id;
    private final Integer countOfBooks;
    private final TotalPrice totalPrice;
    private final PaidAmount paidAmount;
    private final ChangeOfOrder changeOfOrder;
    private final CreditCard creditCard;
    private final LocalDateTime creationDate;
    private final /**@ManyToOne*/
            Customer customer;
    private final /**@ManyToMany*/
            Map<Book, Integer> books;

    private Order(UUID id, Integer countOfBooks, TotalPrice totalPrice, PaidAmount paidAmount,
                  ChangeOfOrder changeOfOrder, CreditCard creditCard, LocalDateTime creationDate, Customer customer,
                  Map<Book, Integer> books) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(countOfBooks);
        Objects.requireNonNull(totalPrice);
        Objects.requireNonNull(paidAmount);
        Objects.requireNonNull(changeOfOrder);
        Objects.requireNonNull(creditCard);
        Objects.requireNonNull(creationDate);
        Objects.requireNonNull(customer);
        Objects.requireNonNull(books);

        if (countOfBooks <= 0) {
            throw new NegativeValueException("Count of books can`t be negative or zero");
        }
        if (totalPrice.totalPrice() < 0.0) {
            throw new NegativeValueException("Total price can`t be negative");
        }
        if (paidAmount.paidAmount() < 0.0) {
            throw new NegativeValueException("Paid amount can`t be negative or smaller than total price in order");
        }
        if (paidAmount.paidAmount() < totalPrice.totalPrice()) {
            throw new InsufficientPaymentException("The paid amount is not enough to complete the order");
        }
        if (books.isEmpty()) {
            throw new IllegalArgumentException("Books can`t be empty");
        }

        this.id = id;
        this.countOfBooks = countOfBooks;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
        this.changeOfOrder = changeOfOrder;
        this.creditCard = creditCard;
        this.creationDate = creationDate;
        this.customer = customer;
        this.books = books;
    }

    public static Order create(UUID id, PaidAmount paidAmount, CreditCard creditCard,
                               LocalDateTime creationDate, Customer customer, Map<Book, Integer> books) {
        Integer countOfBooks = calculateCountOfBooks(books);
        TotalPrice totalPrice = calculateTotalPrice(books);
        ChangeOfOrder changeOfOrder = calculateChange(totalPrice, paidAmount);

        Order order = new Order(
                id,
                countOfBooks,
                totalPrice,
                paidAmount,
                changeOfOrder,
                creditCard,
                creationDate,
                customer,
                Map.copyOf(books)
        );

        customer.addOrder(order);
        books.forEach((book, _) -> book.addOrder(order));

        return order;
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

    private static Integer calculateCountOfBooks(Map<Book, Integer> books) {
        return books.values()
                .stream()
                .reduce(0, Integer::sum);
    }

    private static TotalPrice calculateTotalPrice(Map<Book, Integer> books) {
        Double price = books.entrySet()
                .stream()
                .map(entry -> {
                    int bookCopies = entry.getValue();
                    double priceOfOneCopy = entry.getKey().getPrice().price();
                    return priceOfOneCopy * bookCopies;
                })
                .reduce(0.0, Double::sum);

        return new TotalPrice(price);
    }

    private static ChangeOfOrder calculateChange(TotalPrice totalPrice, PaidAmount paidAmount) {
        return new ChangeOfOrder(paidAmount.paidAmount() - totalPrice.totalPrice());
    }
}