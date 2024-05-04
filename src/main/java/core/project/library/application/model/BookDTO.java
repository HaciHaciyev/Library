package core.project.library.application.model;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Order;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;

//TODO for Nicat
public class BookDTO {
    private @NotNull Title title;
    private @NotNull Description description;
    private @NotNull ISBN isbn;
    private @NotNull BigDecimal price;
    private @NotNull Integer quantityOnHand;
    private @NotNull Category category;
    private @NotNull Events events;
    private /**@ManyToOne*/Publisher publisher;
    private /**@ManyToMany*/Set<Author> authors;
    private /**@ManyToMany*/Set<Order> orders;
}
