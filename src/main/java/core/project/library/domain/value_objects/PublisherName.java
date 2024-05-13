package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record PublisherName(@NotBlank @Size(min = 5, max = 25) String publisherName) {

     public PublisherName(String publisherName) {
         Objects.requireNonNull(publisherName);
         if (publisherName.isBlank()) {
             throw new IllegalArgumentException("Publisher name should`t be blank.");
         }
         if (publisherName.length() < 5 || publisherName.length() > 25) {
             throw new IllegalArgumentException("Publisher name should`t be longer than 25 characters and shorter than 5 characters.");
         }
         this.publisherName = publisherName;
     }
}
