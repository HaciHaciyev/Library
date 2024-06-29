package core.project.library.domain.value_objects;

import jakarta.validation.constraints.Digits;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;
import java.util.Objects;

public record CreditCard(
        @CreditCardNumber(message="Not a valid credit card number")
        String creditCardNumber,
        LocalDate creditCardExpiration,
        @Digits(integer=3, fraction=0, message="Invalid CVV")
        String creditCardCVV
) {

    public CreditCard {
        Objects.requireNonNull(creditCardNumber);
        Objects.requireNonNull(creditCardExpiration);
        Objects.requireNonNull(creditCardCVV);

        // TODO validation
    }
}
