package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Phone(@NotBlank String phoneNumber) {

    public Phone {
        Objects.requireNonNull(phoneNumber);
        if (phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number should`t be blank.");
        }

        String phoneRegex = "[+](\\d{3})( )?(\\d{2})([- ])?(\\d{3})([- ])?(\\d{2})([- ])?(\\d{2})";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid phone number.");
        }
    }
}
