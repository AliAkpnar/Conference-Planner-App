package com.n11.conferenceapp.converter;

import com.n11.conferenceapp.dto.enums.SessionType;
import com.n11.conferenceapp.dto.model.ConferencePlannerModel;
import com.n11.conferenceapp.dto.response.ConferencePlannerResponse;
import com.n11.conferenceapp.persistence.entity.ConferencePlanner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConferencePlannerConverter
{
    public static List<ConferencePlannerResponse> toResponseList(List<ConferencePlanner> entities) {
        List<ConferencePlannerResponse> res = new ArrayList<>();
        if (entities != null) {
            for (ConferencePlanner entity : entities) {
                res.add(toResponse(entity));
            }
        }
        return res;
    }

    public static ConferencePlannerResponse toResponse(ConferencePlanner entity) {
        ConferencePlannerResponse res = new ConferencePlannerResponse();
        res.setName(entity.getName());

        List<ConferencePlannerModel> presentations = new ArrayList<>();
        ConferencePlannerModel model = new ConferencePlannerModel();
        model.setTitle(entity.getTitle());
        model.setStartTime(entity.getStartTime());
        model.setDuration(entity.getDuration());
        if (entity.getSessionType() != null) {
            model.setSessionType(SessionType.valueOf(entity.getSessionType()));
        }
        presentations.add(model);

        res.setPresentations(presentations);
        res.setName(entity.getName());
        return res;
    }

    public static List<ConferencePlanner> toEntityList(List<ConferencePlannerModel> trackPresentations, String conferenceName) {
        List<ConferencePlanner> res = new ArrayList<>();
        if (trackPresentations != null) {
            for (ConferencePlannerModel presentation : trackPresentations) {
                ConferencePlanner entity = new ConferencePlanner();
                entity.setTitle(presentation.getTitle());
                entity.setStartTime(presentation.getStartTime());
                entity.setDuration(presentation.getDuration());
                entity.setSessionType(presentation.getSessionType().name());
                entity.setName(conferenceName);
                res.add(entity);
            }
        }
        return res;
    }
}
