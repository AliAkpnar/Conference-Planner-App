package com.n11.conferenceapp.dto.model;

import com.n11.conferenceapp.dto.enums.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ConferencePlannerModel
{
    private String title;
    private LocalTime startTime;
    private int duration;
    private SessionType sessionType;
}
