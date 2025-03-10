package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.CreditCardExpirationException;
import core.project.library.infrastructure.exceptions.LuhnAlgorithmException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

public record CreditCard(@NotNull @CreditCardNumber String creditCardNumber,
                         @NotNull LocalDate creditCardExpiration) {

    public CreditCard {
        if (creditCardNumber == null || creditCardExpiration == null) {
            throw new NullValueException("Credit card number or expiration cannot be null.");
        }

        boolean isNotExpired = LocalDate.now().isBefore(creditCardExpiration);

        if (!isNotExpired) {
            throw new CreditCardExpirationException("Credit card has expired.");
        }

        boolean isValidLuhn = luhnAlgorithmValidation(creditCardNumber);
        if (!isValidLuhn) {
            throw new LuhnAlgorithmException("Invalid credit card number");
        }
    }

    private boolean luhnAlgorithmValidation(String creditCardNumber) {
        char[] chars = convertToArrayOfValidChars(creditCardNumber);
        return getSum(chars) % 10 == 0;
    }

    private char[] convertToArrayOfValidChars(String input) {
        String sanitized = input.replaceAll("\\D", "");
        return sanitized.toCharArray();
    }

    private int getSum(char[] chars) {
        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            int number = getInReverseOrder(chars, i);
            sum += getElementValue(i, number);
        }
        return sum;
    }

    private int getInReverseOrder(char[] chars, int i) {
        int indexInReverseOrder = chars.length - 1 - i;
        char character = chars[indexInReverseOrder];
        return Character.getNumericValue(character);
    }

    private int getElementValue(int i, int number) {
        if (i % 2 != 0) {
            return getOddElementValue(number);
        } else {
            return number;
        }
    }

    private int getOddElementValue(int element) {
        int value = element * 2;
        if (value <= 9) {
            return value;
        }
        return value - 9;
    }
}
