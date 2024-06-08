package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record PublisherName(@NotBlank @Size(min = 4, max = 50) String publisherName) {

     public PublisherName {
         if (publisherName == null) {
             throw new IllegalArgumentException("Publisher name cannot be null");
         }
         if (publisherName.isBlank()) {
             throw new IllegalArgumentException("Publisher name should`t be blank.");
         }
         if (publisherName.length() < 4 || publisherName.length() > 50) {
             throw new IllegalArgumentException("Publisher name should`t be longer than 50 characters and shorter than 4 characters." +
                     "\n Publisher name: " + publisherName);
         }
     }
}
