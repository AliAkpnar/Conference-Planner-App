package com.n11.conferenceapp.dto.request;

import com.n11.conferenceapp.advice.constant.ErrorMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.n11.conferenceapp.advice.constant.ErrorMessage.DURATION_MIN_ONE;
import static com.n11.conferenceapp.advice.constant.ErrorMessage.NOT_NULL_MESSAGE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConferencePlannerCreatePayload
{
    @NotNull(message = NOT_NULL_MESSAGE)
    @NotBlank(message = NOT_NULL_MESSAGE)
    private String title;
    @Min(value = 1, message = DURATION_MIN_ONE)
    private int duration;
}
