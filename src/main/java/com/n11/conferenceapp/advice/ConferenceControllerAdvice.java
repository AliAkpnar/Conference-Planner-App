package com.n11.conferenceapp.advice;

import com.n11.conferenceapp.advice.exception.BaseRuntimeException;
import com.n11.conferenceapp.advice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ConferenceControllerAdvice
{
    @ExceptionHandler(BaseRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(BaseRuntimeException e) {
        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.of(e));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleException(BindException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(e.getBindingResult()));
    }
}
