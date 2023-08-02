package ru.practicum.ewm.exception;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status("NOT_FOUND")
                .reason("The required object was not found.")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleSQLError(final PSQLException e) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status("CONFLICT")
                .reason("Integrity constraint has been violated.")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictError(final ConflictException e) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status("CONFLICT")
                .reason("For the requested operation the conditions are not met.")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectData(final MethodArgumentNotValidException e) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleNotValidatedData(final ValidationException e) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status("FORBIDDEN")
                .reason("For the requested operation the conditions are not met.")
                .build();
    }

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
