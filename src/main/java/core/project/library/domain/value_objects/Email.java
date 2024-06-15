package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Email(@NotBlank
                    @jakarta.validation.constraints.Email
                    String email) {

    public Email {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email should`t be blank.");
        }

        String emailRegex = "^(\\S+)@(\\S+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email." +
                    "\n Email: " + email);
        }
    }
}
