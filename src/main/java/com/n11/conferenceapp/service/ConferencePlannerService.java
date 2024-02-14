package com.n11.conferenceapp.service;

import com.n11.conferenceapp.advice.exception.InsufficientTimeException;
import com.n11.conferenceapp.advice.exception.PresentationNotFoundException;
import com.n11.conferenceapp.advice.exception.MissingParameterException;
import com.n11.conferenceapp.converter.ConferencePlannerConverter;
import com.n11.conferenceapp.dto.model.ConferencePlannerModel;
import com.n11.conferenceapp.dto.request.ConferencePlannerCreatePayload;
import com.n11.conferenceapp.dto.enums.SessionType;
import com.n11.conferenceapp.dto.response.ConferencePlannerResponse;
import com.n11.conferenceapp.persistence.entity.ConferencePlanner;
import com.n11.conferenceapp.persistence.repository.ConferencePlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ConferencePlannerService
{
    private static final int MORNING_SESSION_DURATION = 180;
    private static final int AFTERNOON_SESSION_DURATION = 240;
    private static final int LUNCH_DURATION = 60;
    private static final int NETWORKING_DURATION = 60;
    private static final LocalTime END_OF_DAY_HOUR = LocalTime.of(17, 0);

    private final ConferencePlannerRepository conferencePlannerRepository;

    @Transactional
    public void planConference(List<ConferencePlannerCreatePayload> presentations) {
        if (CollectionUtils.isEmpty(presentations)) {
            throw new MissingParameterException();
        }
        List<ConferencePlannerModel> trackPresentations = new ArrayList<>();
        List<ConferencePlannerCreatePayload> remainingPresentations = new ArrayList<>(presentations);
        List<ConferencePlannerResponse> conferencePlannerResponses = new ArrayList<>();
        int trackNumber = 1;

        while (!remainingPresentations.isEmpty()) {
            scheduleSession(trackPresentations, remainingPresentations, MORNING_SESSION_DURATION, SessionType.MORNING);
            addLunchEvent(trackPresentations);
            int remainingAfternoonDuration = scheduleSession(trackPresentations, remainingPresentations,
                AFTERNOON_SESSION_DURATION, SessionType.AFTERNOON);
            addNetworkingEvent(trackPresentations, remainingAfternoonDuration);
            conferencePlannerResponses.add(new ConferencePlannerResponse("Track " + trackNumber++, trackPresentations));
            saveConference(trackPresentations, conferencePlannerResponses);
            trackPresentations = new ArrayList<>();
        }
    }

    public Map<String, List<String>> getConferencePlanning() {
        List<ConferencePlanner> conferencePlanners = conferencePlannerRepository.findAll();
        if (CollectionUtils.isEmpty(conferencePlanners)) {
            throw new PresentationNotFoundException();
        }
        Map<String, List<ConferencePlanner>> groupedConferencePlanners = conferencePlanners.stream()
            .collect(Collectors.groupingBy(ConferencePlanner::getName));

        return groupedConferencePlanners.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<ConferencePlannerResponse> conferencePlannerResponses =
                        ConferencePlannerConverter.toResponseList(entry.getValue());
                    return conferencePlannerResponses.stream()
                        .flatMap(response -> response.getPresentations().stream()
                            .map(this::formatPresentation))
                        .toList();
                }
            ));
    }

    private int scheduleSession(List<ConferencePlannerModel> sessionPresentations,
                                List<ConferencePlannerCreatePayload> remainingPresentations,
                                int sessionTimeRemaining,
                                SessionType sessionType) {
        int remainingTime = sessionTimeRemaining;
        List<ConferencePlannerCreatePayload> sortedPresentations = remainingPresentations.stream()
            .sorted(Comparator.comparingInt(ConferencePlannerCreatePayload::getDuration).reversed())
            .collect(Collectors.toList());

        LocalTime sessionStartTime = sessionType == SessionType.MORNING ? LocalTime.of(9, 0) : LocalTime.of(13, 0);

        for (ConferencePlannerCreatePayload presentation : sortedPresentations) {
            int presentationDuration = presentation.getDuration();
            boolean isLightningTalk = presentationDuration < 10;
            if (isLightningTalk) {
                presentationDuration = presentationDuration <= remainingTime ? presentationDuration : 5;
            }
            if (presentationDuration <= remainingTime) {
                addPresentation(sessionPresentations, sessionStartTime, presentation, sessionType);
                sessionStartTime = sessionStartTime.plusMinutes(presentationDuration);
                remainingTime -= presentationDuration;
                remainingPresentations.remove(presentation);
            }
        }

        return remainingTime;
    }

    private void addLunchEvent(List<ConferencePlannerModel> trackPresentations) {
        ConferencePlannerModel lunch = new ConferencePlannerModel();
        lunch.setTitle("Lunch");
        lunch.setStartTime(LocalTime.of(12, 0));
        lunch.setDuration(LUNCH_DURATION);
        lunch.setSessionType(SessionType.LUNCH);
        trackPresentations.add(lunch);
    }

    private void addNetworkingEvent(List<ConferencePlannerModel> trackPresentations, int remainingAfternoonDuration) {
        ConferencePlannerModel lastPresentation = getLastPresentation(trackPresentations);
        LocalTime networkingStartTime = calculateNetworkingStartTime(lastPresentation);

        if (networkingStartTime != null && remainingAfternoonDuration >= NETWORKING_DURATION
            && networkingStartTime.plusMinutes(NETWORKING_DURATION).isBefore(END_OF_DAY_HOUR)) {
            trackPresentations.add(createNetworkingEvent(networkingStartTime, NETWORKING_DURATION));
        } else if (remainingAfternoonDuration < 0) {
            throw new InsufficientTimeException();
        } else {
            trackPresentations.add(createNetworkingEvent(
                networkingStartTime,
                Math.min(remainingAfternoonDuration, NETWORKING_DURATION)
            ));
        }
        ensureNetworkingEventDoesNotEndAfterFive(trackPresentations);
    }

    private void saveConference(List<ConferencePlannerModel> trackPresentations,
                                List<ConferencePlannerResponse> conferencePlannerResponses) {
        List<ConferencePlanner> conferencePlanners =
            ConferencePlannerConverter.toEntityList(trackPresentations,
                conferencePlannerResponses.get(conferencePlannerResponses.size() - 1).getName());
        conferencePlannerRepository.saveAll(conferencePlanners);
    }

    private void addPresentation(List<ConferencePlannerModel> sessionPresentations, LocalTime sessionStartTime,
                                 ConferencePlannerCreatePayload presentation, SessionType sessionType) {
        ConferencePlannerModel conferencePlannerModel = new ConferencePlannerModel();
        conferencePlannerModel.setTitle(presentation.getTitle());
        conferencePlannerModel.setStartTime(sessionStartTime);
        conferencePlannerModel.setDuration(presentation.getDuration());
        conferencePlannerModel.setSessionType(sessionType);
        sessionPresentations.add(conferencePlannerModel);
    }

    private ConferencePlannerModel getLastPresentation(List<ConferencePlannerModel> trackPresentations) {
        return trackPresentations.get(trackPresentations.size() - 1);
    }

    private LocalTime calculateNetworkingStartTime(ConferencePlannerModel lastPresentation) {
        LocalTime networkingStartTime = lastPresentation.getStartTime().plusMinutes(lastPresentation.getDuration());
        return networkingStartTime.isBefore(LocalTime.of(16, 0)) ?
            LocalTime.of(16, 0) : networkingStartTime;
    }

    private ConferencePlannerModel createNetworkingEvent(LocalTime startTime, int duration) {
        ConferencePlannerModel networkingEvent = new ConferencePlannerModel();
        networkingEvent.setTitle("Networking Event");
        networkingEvent.setStartTime(startTime);
        networkingEvent.setDuration(duration);
        networkingEvent.setSessionType(SessionType.NETWORKING);
        return networkingEvent;
    }

    private void ensureNetworkingEventDoesNotEndAfterFive(List<ConferencePlannerModel> trackPresentations) {
        ConferencePlannerModel networkingEvent = getLastPresentation(trackPresentations);
        if (networkingEvent.getStartTime().plusMinutes(networkingEvent.getDuration()).isAfter(LocalTime.of(17, 0))) {
            networkingEvent.setDuration(LocalTime.of(17, 0)
                .minusMinutes(networkingEvent.getStartTime().toSecondOfDay() / 60)
                .getMinute());
        }
    }

    private String formatPresentation(ConferencePlannerModel presentation) {
        String formattedTime = formatTime(presentation.getStartTime());
        return " " + formattedTime + " " + presentation.getTitle() + " " + presentation.getDuration() + "min";
    }

    private String formatTime(LocalTime time) {
        String suffix = time.isBefore(LocalTime.NOON) ? "AM" : "PM";
        return String.format("%02d:%02d%s", time.getHour(), time.getMinute(), suffix);
    }
}