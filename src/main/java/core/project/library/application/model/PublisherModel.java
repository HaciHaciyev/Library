package core.project.library.application.model;

import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PublisherModel(@NotNull @Valid PublisherName publisherName,
                             @NotNull @Valid Address address,
                             @NotNull @Valid Phone phone,
                             @NotNull @Valid Email email,
                             @NotNull @Valid Set<BookDTO> books) {
}
