package com.n11.conferenceapp.dto.response;


import com.n11.conferenceapp.dto.model.ConferencePlannerModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConferencePlannerResponse
{
    private String name;
    private List<ConferencePlannerModel> presentations;
}
