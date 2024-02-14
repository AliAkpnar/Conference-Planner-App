package com.n11.conferenceapp.advice.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodes
{
    public static final int NOT_FOUND = 9000;
    public static final int INSUFFICIENT_TIME = 9001;
    public static final int MISSING_PARAMETER = 9002;
    public static final int VALIDATION_ERROR = 9999;
}
