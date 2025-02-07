package com.example.planes.handler;

import com.example.planes.exception.InvalidEntityDataException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class PlaneServiceExceptionHandler {

    @ExceptionHandler(value = InvalidEntityDataException.class)
    protected ResponseEntity<Object> handleInvalidEntityDataException(InvalidEntityDataException ex) {

        ExceptionHandlerResponse exceptionHandlerResponse = new ExceptionHandlerResponse(ex.getMessage(), ex.getErrorCode(), ex.getStatus());
        return new ResponseEntity<>(exceptionHandlerResponse, ex.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ExceptionHandlerResponse {
        private String message;
        private String errorCode;
        private HttpStatus status;
    }
}




