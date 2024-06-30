package core.project.library.application.model;

import core.project.library.domain.value_objects.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record BookModel(@NotNull @Valid Title title,
                        @NotNull @Valid Description description,
                        @NotNull @Valid ISBN isbn,
                        @NotNull @Valid Price price,
                        @NotNull @Valid QuantityOnHand quantityOnHand,
                        @NotNull Category category,
                        @NotNull @Valid PublisherDTO publisher,
                        @NotNull @Valid Set<AuthorDTO> authors) {
}

