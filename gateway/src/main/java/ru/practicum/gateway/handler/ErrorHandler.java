package ru.practicum.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handle(final MethodArgumentTypeMismatchException exc) {
        String message = exc.getMessage();
        log.warn("Произошла ошибка при валидации данных: {}", exc.getMostSpecificCause().getMessage());
        return Map.of("error", "Unknown state: " + message.substring(message.lastIndexOf(".") + 1));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handle(final MethodArgumentNotValidException exc) {
        List<String> errors = exc.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.warn("Произошла ошибка при валидации данных: {}", errors);
        return Map.of("error", errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final ConstraintViolationException exc) {
        log.warn("Произошла ошибка при валидации данных: {}", exc.getMessage());
        return Map.of("error", exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final IllegalArgumentException exc) {
        log.warn("Возникла ошибка при валидации данных: {}", exc.getMessage());
        return Map.of("error", exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handle(final Throwable exc) {
        log.warn("Возникла непредвиденная ошибка: {}", exc.getMessage());
        return Map.of("error", "Возникла непредвиденная ошибка");
    }
}