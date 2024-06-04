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

        String phoneRegex = "[+](\\d{1,3})( )?(\\d{2})([- ])?(\\d{3})([- ])?(\\d{2})([- ])?(\\d{2})";
        String comprehensiveRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        Pattern pattern = Pattern.compile(comprehensiveRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid phone number.\n Phone number: " + phoneNumber);
        }
    }
}
