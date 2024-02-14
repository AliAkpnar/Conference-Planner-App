package com.n11.conferenceapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.n11.conferenceapp.util.MessageCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {
    private Integer messageCode = MessageCode.SUCCESS;
    private String errorStack;
    private T body;
}
