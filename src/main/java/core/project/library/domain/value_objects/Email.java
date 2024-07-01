package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Email(@NotBlank
                    @jakarta.validation.constraints.Email
                    String email) {

    public Email {
        if (email == null) {
            throw new NullValueException("Email can`t be null");
        }
        if (email.isBlank()) {
            throw new BlankValueException("Email should`t be blank.");
        }

        String emailRegex = "^(\\S+)@(\\S+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new InvalidSizeException(String.format("Invalid email format: %s", email));
        }
    }
}
