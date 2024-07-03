package core.project.library.infrastructure.exceptions.handlers;

import core.project.library.infrastructure.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BlankValueException.class)
    public String handleBlankValueException(HttpServletRequest request, BlankValueException e) {
        log.info("BlankValueException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CreditCardExpirationException.class)
    public String handleCreditCardExpirationException(HttpServletRequest request, CreditCardExpirationException e) {
        log.info("CreditCardException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientPaymentException.class)
    public String handleInsufficientPaymentException(HttpServletRequest request, InsufficientPaymentException e) {
        log.info("InsufficientPaymentException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidEmailException.class)
    public String handleInvalidEmailException(HttpServletRequest request, InvalidEmailException e) {
        log.info("InvalidEmailException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidIsbnException.class)
    public String handleInvalidIsbnException(HttpServletRequest request, InvalidIsbnException e) {
        log.info("InvalidIsbnException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPhoneException.class)
    public String handleInvalidPhoneException(HttpServletRequest request, InvalidPhoneException e) {
        log.info("InvalidPhoneException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidSizeException.class)
    public String handleInvalidSizeException(HttpServletRequest request, InvalidSizeException e) {
        log.info("InvalidSizeException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LuhnAlgorithmException.class)
    public String handleLuhnAlgorithmException(HttpServletRequest request, LuhnAlgorithmException e) {
        log.info("luhnAlgorithmException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NegativeValueException.class)
    public String handleUnknownException(HttpServletRequest request, NegativeValueException e) {
        log.info("NegativeValueException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleUnknownException(HttpServletRequest request, NotFoundException e) {
        log.info("NotFoundException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullValueException.class)
    public String handleNullValueException(HttpServletRequest request, NullValueException e) {
        log.info("NullValueException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(QuantityOnHandException.class)
    public String handleQuantityOnHandException(HttpServletRequest request, QuantityOnHandException e) {
        log.info("QuantityOnHandException: {}. In the request: {}", e.getMessage(), request.toString());
        return e.getMessage();
    }

}
