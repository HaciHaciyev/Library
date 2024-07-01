package core.project.library.infrastructure.exceptions;

public class CreditCardExpirationException extends RuntimeException {
    public CreditCardExpirationException(String message) {
        super(message);
    }
}
