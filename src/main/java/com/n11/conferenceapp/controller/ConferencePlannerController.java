package com.n11.conferenceapp.controller;

import com.n11.conferenceapp.dto.request.ConferencePlannerCreatePayload;
import com.n11.conferenceapp.dto.response.GenericResponse;
import com.n11.conferenceapp.service.ConferencePlannerService;
import com.n11.conferenceapp.util.MessageCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/conferences")
@RequiredArgsConstructor
public class ConferencePlannerController
{
    private final ConferencePlannerService conferencePlannerService;

    @PostMapping("/planning")
    public ResponseEntity<GenericResponse<Void>> planConference(@Valid @RequestBody List<ConferencePlannerCreatePayload> presentations) {
        conferencePlannerService.planConference(presentations);
        return new ResponseEntity<>(GenericResponse.<Void>builder()
            .messageCode(MessageCode.SUCCESS)
            .build(),
            HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<Map<String, List<String>>>> getConferencePlanning() {
        return new ResponseEntity<>(GenericResponse.<Map<String, List<String>>>builder()
            .body(conferencePlannerService.getConferencePlanning())
            .messageCode(MessageCode.SUCCESS)
            .build(),
            HttpStatus.OK);
    }
}
