package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

public record ISBN(@org.hibernate.validator.constraints.ISBN
                   @NotBlank
                   String isbn) {

}
