package core.project.library.infrastructure.data_transfer;

import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.Category;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Title;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;


class BookDtoTest {
    
    @Test
    void rejectsNullId() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullId);
    }

    private void dtoWithNullId() {
        new BookDTO(null, UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullPublisherId() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullPublisherId);
    }

    private void dtoWithNullPublisherId() {
        new BookDTO(UUID.randomUUID(), null, new Title("Title"), new Description("Desription"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullTitle() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullTitle);
    }

    private void dtoWithNullTitle() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), null, new Description("Desription"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullInTitle() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullInTitle);
    }

    private void dtoWithNullInTitle() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title(null), new Description("Desription"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullDescription() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullDescription);
    }

    private void dtoWithNullDescription() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), null,
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullInDescription() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullInDescription);
    }

    private void dtoWithNullInDescription() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description(null),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullISBN() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullISBN);
    }

    private void dtoWithNullISBN() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                null, new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullInISBN() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullInISBN);
    }

    private void dtoWithNullInISBN() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN(null), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullPrice() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullPrice);
    }

    private void dtoWithNullPrice() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), null, 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullQuantityOnHand() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullQuantityOnHand);
    }

    private void dtoWithNullQuantityOnHand() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), null, Category.Adventure, new Events());
    }

    @Test
    void rejectsNullCategory() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullCategory);
    }

    private void dtoWithNullCategory() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, null, new Events());
    }

    @Test
    void rejectsNullEvents() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullEvents);
    }

    private void dtoWithNullEvents() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, null);
    }

    @Test
    void rejectsNullInEvents() {
        Assertions.assertThrows(NullPointerException.class, this::dtoWithNullInEvents);
    }

    private void dtoWithNullInEvents() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure,
                new Events(null, null));
    }

    @Test
    void rejectsNegativePrice() {
        Assertions.assertThrows(IllegalArgumentException.class, this::dtoWithIllegalPrice);
    }

    private void dtoWithIllegalPrice() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(-1), 1, Category.Adventure, new Events());
    }

    @Test
    void acceptsPositivePrice() {
        Assertions.assertDoesNotThrow(this::dtoWithPositivePrice);
    }

    private void dtoWithPositivePrice() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }

    @Test
    void rejectsNegativeQuantity() {
        Assertions.assertThrows(IllegalArgumentException.class, this::dtoWithNegativeQuantity);
    }

    private void dtoWithNegativeQuantity() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), -1, Category.Adventure, new Events());
    }

    @Test
    void acceptsPositiveQuantity() {
        Assertions.assertDoesNotThrow(this::dtoWithPositiveQuantity);
    }

    private void dtoWithPositiveQuantity() {
        new BookDTO(UUID.randomUUID(), UUID.randomUUID(), new Title("Title"), new Description("Description"),
                new ISBN("9781861972712"), new BigDecimal(1), 1, Category.Adventure, new Events());
    }
}
