package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublisherName(@NotBlank @Size(max = 25) String publisherName) {
}
