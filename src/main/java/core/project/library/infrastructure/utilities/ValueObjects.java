package core.project.library.infrastructure.utilities;

import core.project.library.domain.value_objects.*;
import net.datafaker.Faker;

import java.math.BigDecimal;

public class ValueObjects {

    private static final Faker faker = new Faker();

    public static Address randomAddress() {
        return new Address(
                faker.address().state(),
                faker.address().city(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress()
        );
    }

    public static Category randomCategory() {
        int randomCategory = faker.number().numberBetween(0, Category.values().length);
        return Category.values()[randomCategory];
    }

    public static Description randomDescription() {
        return new Description(faker.text().text(
                15,
                255,
                true,
                true,
                true)
        );
    }

    public static Email randomEmail() {
        return new Email(faker.internet().emailAddress());
    }

    public static FirstName randomFirstName() {
        return new FirstName(faker.name().firstName());
    }

    public static ISBN randomISBN13() {
        return new ISBN("9781861972712");
    }

    public static LastName randomLastName() {
        return new LastName(faker.name().lastName());
    }

    public static Password randomPassword() {
        return new Password(faker.internet().password(5, 48));
    }

    public static Phone randomPhone() {
        return new Phone(faker.phoneNumber().phoneNumberNational());
    }

    public static PublisherName randomPublisherName() {
        return new PublisherName(faker.book().publisher());
    }

    public static Title randomTitle() {
        return new Title(faker.book().title());
    }

    public static TotalPrice randomTotalPrice() {
        return new TotalPrice(BigDecimal.valueOf(faker.number().numberBetween(1, 5000)));
    }
}
