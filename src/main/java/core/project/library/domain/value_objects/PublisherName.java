package core.project.library.domain.value_objects;

import core.project.library.infrastructure.exceptions.BlankValueException;
import core.project.library.infrastructure.exceptions.InvalidSizeException;
import core.project.library.infrastructure.exceptions.NullValueException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublisherName(@NotBlank @Size(min = 4, max = 50) String publisherName) {

     public PublisherName {
         if (publisherName == null) {
             throw new NullValueException("Publisher name can`t be null");
         }
         if (publisherName.isBlank()) {
             throw new BlankValueException("Publisher name should`t be blank.");
         }
         if (publisherName.length() < 4 || publisherName.length() > 50) {
             throw new InvalidSizeException("Publisher name should`t be longer than 50 characters and shorter than 4 characters.");
         }
     }
}
