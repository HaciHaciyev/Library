package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record Address(@NotBlank @Size(max = 51) String state,
                      @NotBlank @Size(max = 51) String city,
                      @NotBlank @Size(max = 51) String street,
                      @NotBlank @Size(max = 51) String home) {

    public Address {
        if (state == null || city == null || street == null || home == null) {
            throw new NullValueException("Address fields cannot be null.");
        }
        if (state.isBlank() || city.isBlank() || street.isBlank() || home.isBlank()) {
            throw new BlankValueException("Address fields cannot be blank.");
        }
        if (state.length() > 51 || city.length() > 51 || street.length() > 51 || home.length() > 51) {
            throw new InvalidSizeException("Address fields can`t be longer than 51 characters.");
        }
    }
}
