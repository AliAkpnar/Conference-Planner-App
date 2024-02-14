package com.n11.conferenceapp.advice.exception;

import org.springframework.http.HttpStatus;

import static com.n11.conferenceapp.advice.constant.ErrorCodes.NOT_FOUND;

public class PresentationNotFoundException extends BaseRuntimeException
{
    public PresentationNotFoundException() {
        super(NOT_FOUND, HttpStatus.NOT_FOUND, "There are no presentations to schedule!");
    }
}
