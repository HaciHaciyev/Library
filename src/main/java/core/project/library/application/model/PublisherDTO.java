package core.project.library.application.model;

import core.project.library.domain.value_objects.Address;
import core.project.library.domain.value_objects.Email;
import core.project.library.domain.value_objects.Phone;
import core.project.library.domain.value_objects.PublisherName;
import jakarta.validation.constraints.NotNull;

public record PublisherDTO(@NotNull PublisherName publisherName,
                           @NotNull Address address,
                           @NotNull Phone phone,
                           @NotNull Email email) {
}
