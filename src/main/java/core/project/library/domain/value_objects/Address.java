package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record Address(@NotBlank @Size(max = 51) String state,
                      @NotBlank @Size(max = 51) String city,
                      @NotBlank @Size(max = 51) String street,
                      @NotBlank @Size(max = 51) String home) {

    public Address {
        if (state == null || state.isBlank() || state.length() > 51) {
            throw new IllegalArgumentException("State is either null, blank or invalid");
        }
        if (city == null || city.isBlank() || city.length() > 51) {
            throw new IllegalArgumentException("City is either null, blank or invalid");
        }
        if (street == null || street.isBlank() || street.length() > 51) {
            throw new IllegalArgumentException("Street is either null, blank or invalid");
        }
        if (home == null || home.isBlank() || home.length() > 51) {
            throw new IllegalArgumentException("Home is either null, blank or invalid");
        }
    }
}
