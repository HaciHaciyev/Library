package core.project.library.infrastructure.exceptions.handlers;

import core.project.library.infrastructure.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BlankValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBlankValueException(HttpServletRequest request, BlankValueException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(CreditCardExpirationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCreditCardExpirationException(HttpServletRequest request, CreditCardExpirationException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(InsufficientPaymentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInsufficientPaymentException(HttpServletRequest request, InsufficientPaymentException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(InvalidEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidEmailException(HttpServletRequest request, InvalidEmailException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(InvalidIsbnException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidIsbnException(HttpServletRequest request, InvalidIsbnException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(InvalidPhoneException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidPhoneException(HttpServletRequest request, InvalidPhoneException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(InvalidSizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidSizeException(HttpServletRequest request, InvalidSizeException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(LuhnAlgorithmException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleLuhnAlgorithmException(HttpServletRequest request, LuhnAlgorithmException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(NegativeValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUnknownException(HttpServletRequest request, NegativeValueException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUnknownException(HttpServletRequest request, NotFoundException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNullValueException(HttpServletRequest request, NullValueException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

    @ExceptionHandler(QuantityOnHandException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleQuantityOnHandException(HttpServletRequest request, QuantityOnHandException e) {
        System.out.println(request.toString());
        return e.getMessage();
    }

}
