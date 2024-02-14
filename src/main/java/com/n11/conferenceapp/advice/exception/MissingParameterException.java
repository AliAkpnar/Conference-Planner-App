package com.n11.conferenceapp.advice.exception;

import org.springframework.http.HttpStatus;

import static com.n11.conferenceapp.advice.constant.ErrorCodes.MISSING_PARAMETER;

public class MissingParameterException extends BaseRuntimeException
{
    public MissingParameterException() {
        super(MISSING_PARAMETER, HttpStatus.BAD_REQUEST, "Presentations cannot be empty!");
    }
}
