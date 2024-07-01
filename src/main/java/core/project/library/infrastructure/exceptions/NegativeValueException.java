package core.project.library.infrastructure.exceptions;

public class NegativeValueException extends RuntimeException{
    public NegativeValueException(String message) {
        super(message);
    }
}
