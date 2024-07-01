package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidPhoneException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Phone(@NotBlank String phoneNumber) {

    public Phone {
        if (phoneNumber == null) {
            throw new NullValueException("Phone number can`t be null");
        }
        if (phoneNumber.isBlank()) {
            throw new BlankValueException("Phone number should`t be blank.");
        }

        String patternForAzerbaijaniPhone = "^(\\+\\d{1,3}( )(\\d{2}[- ]?)(\\d{3}[- ]?)(\\d{2}[- ]?)(\\d{2}))$";

        Pattern pattern = Pattern.compile(patternForAzerbaijaniPhone);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            throw new InvalidPhoneException("Invalid phone number.\n Phone number: " + phoneNumber);
        }
    }
}
