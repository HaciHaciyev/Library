package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Phone(@NotBlank String phoneNumber) {

    public Phone {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }
        if (phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number should`t be blank.");
        }

        String patternForAzerbaijaniPhone = "^(\\+\\d{1,3}( )(\\d{2}[- ]?)(\\d{3}[- ]?)(\\d{2}[- ]?)(\\d{2}))$";

        Pattern pattern = Pattern.compile(patternForAzerbaijaniPhone);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid phone number.\n Phone number: " + phoneNumber);
        }
    }
}
