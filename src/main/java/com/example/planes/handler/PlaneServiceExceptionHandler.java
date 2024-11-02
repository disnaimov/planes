package com.example.planes.handler;

import com.example.planes.exception.InvalidEntityDataException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PlaneServiceExceptionHandler {

    @ExceptionHandler(value = InvalidEntityDataException.class)
    protected ResponseEntity<Object> handleInvalidEntityDataException(InvalidEntityDataException ex) {

        ExceptionHandlerResponse exceptionHandlerResponse = new ExceptionHandlerResponse(ex.getMessage(), ex.getErrorCode(), ex.getStatus());
        return new ResponseEntity<>(exceptionHandlerResponse, ex.getStatus());
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
