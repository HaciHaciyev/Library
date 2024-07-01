package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidIsbnException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;

public record ISBN(@org.hibernate.validator.constraints.ISBN
                   @NotBlank
                   String isbn) {

    public ISBN {
        if (isbn == null) {
            throw new NullValueException("ISBN can`t be null");
        }
        if (isbn.isBlank()) {
            throw new BlankValueException("ISBN should`t be blank.");
        }
        if (!isIsbn13Valid(isbn)) {
            throw new InvalidIsbnException("Invalid ISBN number.");
        }
    }

    private static boolean isIsbn13Valid(String isbn) {
        if (isbn.length() != 13) return false;

        int lastDigit;
        char lastCharacter = isbn.charAt(
                isbn.length() - 1
        );

        if (Character.isDigit(lastCharacter)) {
            lastDigit = Character.getNumericValue(
                    isbn.charAt(isbn.length() - 1)
            );
        } else {
            return false;
        }

        int keyValue;
        int someOfTwelve = 0;
        for (int i = 0; i < isbn.length() - 1; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }

            int current = Character.getNumericValue(c);
            if (i % 2 != 0) {
                current = current * 3;
            }

            someOfTwelve += current;
        }

        keyValue = 10 - someOfTwelve % 10;
        return keyValue == lastDigit;
    }
}
