package com.mvpmatch.vending.exception.handler;

import com.mvpmatch.vending.exception.ChangeReturnException;
import com.mvpmatch.vending.exception.ProductAmountUnavailableException;
import com.mvpmatch.vending.exception.ProductNotFoundException;
import com.mvpmatch.vending.exception.RoleNotFoundException;
import com.mvpmatch.vending.exception.UserAlreadyExistsException;
import com.mvpmatch.vending.exception.UserNotEnoughFundsException;
import com.mvpmatch.vending.exception.UserNotFoundException;
import com.mvpmatch.vending.exception.UserUpdateException;
import jakarta.validation.ConstraintViolationException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorBody> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                             .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    protected ResponseEntity<ErrorBody> handleWebExchangeBindException(WebExchangeBindException e) {
        var fieldError = Objects.requireNonNull(e.getFieldError());
        String message = MessageFormat.format("Constraint violation on field ''{0}'': {1}",
                fieldError.getField(), fieldError.getDefaultMessage()
        );
        return ResponseEntity.badRequest()
                .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler({UserNotFoundException.class, RoleNotFoundException.class, ProductNotFoundException.class})
    protected ResponseEntity<ErrorBody> handleNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler({UserAlreadyExistsException.class, UserUpdateException.class,
                       ProductAmountUnavailableException.class, UserNotEnoughFundsException.class,
                       ChangeReturnException.class})
    protected ResponseEntity<ErrorBody> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorBody(HttpStatus.CONFLICT, e.getMessage()));
    }

    @Value
    private static class ErrorBody {
        HttpStatus status;

        String message;

        Instant timestamp;

        public ErrorBody(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
            this.timestamp = Instant.now();
        }
    }
}
