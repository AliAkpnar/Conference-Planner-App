package com.n11.conferenceapp.advice.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {
    public static final String NOT_NULL_MESSAGE = "It must not be null or empty";
    public static final String DURATION_MIN_ONE = "Duration must be minimum 1";
}
