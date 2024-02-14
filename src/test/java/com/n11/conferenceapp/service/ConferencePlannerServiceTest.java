package com.n11.conferenceapp.service;

import com.n11.conferenceapp.advice.exception.MissingParameterException;
import com.n11.conferenceapp.advice.exception.PresentationNotFoundException;
import com.n11.conferenceapp.dto.request.ConferencePlannerCreatePayload;
import com.n11.conferenceapp.persistence.entity.ConferencePlanner;
import com.n11.conferenceapp.persistence.repository.ConferencePlannerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConferencePlannerServiceTest
{
    @InjectMocks
    private ConferencePlannerService conferencePlannerService;

    @Mock
    private ConferencePlannerRepository conferencePlannerRepository;

    @Test
    void shouldReturnMissingParameterExceptionWhenPresentationsIsEmpty() {
        // Given
        List<ConferencePlannerCreatePayload> presentations = Collections.emptyList();

        // When & Then
        assertThrows(MissingParameterException.class, () -> conferencePlannerService.planConference(presentations));
    }

    @Test
    void shouldReturnSuccessWhenPresentationsAreScheduled() {
        // Given
        ConferencePlannerCreatePayload presentation1 = new ConferencePlannerCreatePayload();
        presentation1.setDuration(180);
        presentation1.setTitle("Presentation 1");
        ConferencePlannerCreatePayload presentation2 = new ConferencePlannerCreatePayload();
        presentation2.setDuration(240);
        presentation2.setTitle("Presentation 2");
        List<ConferencePlannerCreatePayload> presentations = Arrays.asList(presentation1, presentation2);

        // When
        conferencePlannerService.planConference(presentations);

        // Then
        ArgumentCaptor<List<ConferencePlanner>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(conferencePlannerRepository, times(1)).saveAll(argumentCaptor.capture());

        List<ConferencePlanner> savedConferencePlanners = argumentCaptor.getValue();
        assertAll("Saved conference planners",
                () -> assertEquals(4, savedConferencePlanners.size()),
                () -> assertEquals("Presentation 1", savedConferencePlanners.get(0).getTitle()),
                () -> assertEquals(180, savedConferencePlanners.get(0).getDuration()),
                () -> assertEquals("Lunch", savedConferencePlanners.get(1).getTitle()),
                () -> assertEquals(60, savedConferencePlanners.get(1).getDuration()),
                () -> assertEquals("Presentation 2", savedConferencePlanners.get(2).getTitle()),
                () -> assertEquals(240, savedConferencePlanners.get(2).getDuration()),
                () -> assertEquals("Networking Event", savedConferencePlanners.get(3).getTitle()),
                () -> assertEquals(0, savedConferencePlanners.get(3).getDuration()));
    }

    @Test
    void shouldReturnSuccessWhenPresentationsAreScheduledWithLightningTalk() {
        // Given
        ConferencePlannerCreatePayload lightningTalk = new ConferencePlannerCreatePayload();
        lightningTalk.setDuration(5);
        lightningTalk.setTitle("Lightning Talk");
        List<ConferencePlannerCreatePayload> presentations = Collections.singletonList(lightningTalk);

        // When
        conferencePlannerService.planConference(presentations);

        // Then
        ArgumentCaptor<List<ConferencePlanner>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(conferencePlannerRepository, times(1)).saveAll(argumentCaptor.capture());

        List<ConferencePlanner> savedConferencePlanners = argumentCaptor.getValue();
        assertAll("Saved conference planners",
            () -> assertEquals(3, savedConferencePlanners.size()),
            () -> assertEquals("Lightning Talk", savedConferencePlanners.get(0).getTitle()),
            () -> assertEquals(5, savedConferencePlanners.get(0).getDuration()));
    }

    @Test
    void shouldReturnSuccessWhenPresentationsAreScheduledWithSufficeNetworkingEvent() {
        // Given
        ConferencePlannerCreatePayload presentation1 = new ConferencePlannerCreatePayload();
        presentation1.setDuration(180);
        ConferencePlannerCreatePayload presentation2 = new ConferencePlannerCreatePayload();
        presentation2.setDuration(180);
        List<ConferencePlannerCreatePayload> presentations = Arrays.asList(presentation1, presentation2);

        // When
        conferencePlannerService.planConference(presentations);

        // Then
        ArgumentCaptor<List<ConferencePlanner>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(conferencePlannerRepository, times(1)).saveAll(argumentCaptor.capture());

        List<ConferencePlanner> savedConferencePlanners = argumentCaptor.getValue();
        assertAll("Saved conference planners",
            () -> assertEquals(4, savedConferencePlanners.size()),
            () -> assertEquals("Networking Event", savedConferencePlanners.get(3).getTitle()),
            () -> assertEquals(60, savedConferencePlanners.get(3).getDuration()));
    }

    @Test
    void shouldReturnSuccessWhenPlanConferenceWithExactTotalDuration() {
        // Given
        ConferencePlannerCreatePayload presentation1 = new ConferencePlannerCreatePayload();
        presentation1.setDuration(180);
        ConferencePlannerCreatePayload presentation2 = new ConferencePlannerCreatePayload();
        presentation2.setDuration(240);
        List<ConferencePlannerCreatePayload> presentations = Arrays.asList(presentation1, presentation2);

        // When & Then
        assertDoesNotThrow(() -> conferencePlannerService.planConference(presentations));
    }

    @Test
    void shouldReturnPlanConferenceWithNetworkingEventEndingAfterFive() {
        // Given
        ConferencePlannerCreatePayload presentation1 = new ConferencePlannerCreatePayload();
        presentation1.setDuration(180);
        ConferencePlannerCreatePayload presentation2 = new ConferencePlannerCreatePayload();
        presentation2.setDuration(180);
        List<ConferencePlannerCreatePayload> presentations = Arrays.asList(presentation1, presentation2);

        // When & Then
        assertDoesNotThrow(() -> conferencePlannerService.planConference(presentations));
    }

    @Test
    void shouldReturnSuccessWhenNetworkingEventWithSufficientTime() {
        // Given
        ConferencePlannerCreatePayload presentation1 = new ConferencePlannerCreatePayload();
        presentation1.setDuration(180); // Duration equal to morning session
        ConferencePlannerCreatePayload presentation2 = new ConferencePlannerCreatePayload();
        presentation2.setDuration(180); // Duration equal to afternoon session
        List<ConferencePlannerCreatePayload> presentations = Arrays.asList(presentation1, presentation2);

        // When
        conferencePlannerService.planConference(presentations);

        // Then
        ArgumentCaptor<List<ConferencePlanner>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(conferencePlannerRepository, times(1)).saveAll(argumentCaptor.capture());

        List<ConferencePlanner> savedConferencePlanners = argumentCaptor.getValue();
        assertAll("Saved conference planners",
            () -> assertEquals(4, savedConferencePlanners.size()),
            () -> assertEquals("Networking Event", savedConferencePlanners.get(3).getTitle()),
            () -> assertEquals(60, savedConferencePlanners.get(3).getDuration()));
    }

    @Test
    void shouldReturnSuccessWhenGetConferencePlanning() {
        // Given
        ConferencePlanner conferencePlanner1 = new ConferencePlanner();
        conferencePlanner1.setName("Track 1");
        conferencePlanner1.setTitle("Presentation 1");
        conferencePlanner1.setDuration(180);
        conferencePlanner1.setStartTime(LocalTime.of(9, 0));
        ConferencePlanner conferencePlanner2 = new ConferencePlanner();
        conferencePlanner2.setName("Track 1");
        conferencePlanner2.setTitle("Presentation 2");
        conferencePlanner2.setDuration(240);
        conferencePlanner2.setStartTime(LocalTime.of(13, 0));
        List<ConferencePlanner> conferencePlanners = Arrays.asList(conferencePlanner1, conferencePlanner2);

        when(conferencePlannerRepository.findAll()).thenReturn(conferencePlanners);

        // When
        Map<String, List<String>> result = conferencePlannerService.getConferencePlanning();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Track 1"));
        List<String> presentations = result.get("Track 1");
        assertEquals(2, presentations.size());
        assertTrue(presentations.contains(" 09:00AM Presentation 1 180min"));
        assertTrue(presentations.contains(" 13:00PM Presentation 2 240min"));
    }

    @Test
    void shouldThrowExceptionWhenGetConferencePlanningWithEmptyList() {
        // Given
        when(conferencePlannerRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(PresentationNotFoundException.class, () -> conferencePlannerService.getConferencePlanning());
    }
}
