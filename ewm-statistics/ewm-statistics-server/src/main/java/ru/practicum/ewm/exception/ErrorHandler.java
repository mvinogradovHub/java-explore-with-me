package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotValidatedParam(final ParameterRequestException e) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .build();
    }

}
