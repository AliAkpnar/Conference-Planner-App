package com.n11.conferenceapp.advice.exception;

import org.springframework.http.HttpStatus;

import static com.n11.conferenceapp.advice.constant.ErrorCodes.INSUFFICIENT_TIME;

public class InsufficientTimeException extends BaseRuntimeException
{
    public InsufficientTimeException() {
        super(INSUFFICIENT_TIME, HttpStatus.CONFLICT, "There is not enough time for the presentations!");
    }
}
