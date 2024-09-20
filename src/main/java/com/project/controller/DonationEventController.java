package com.project.controller;

import com.project.model.DonationEvent;
import com.project.service.DonationEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
public class DonationEventController {

    @Autowired
    private DonationEventService eventService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody DonationEvent event) {
        DonationEvent createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PostMapping("/{eventId}/register/{donorId}")
    public ResponseEntity<?> registerDonorToEvent(@PathVariable String eventId, @PathVariable String donorId) {
        eventService.registerDonor(eventId, donorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
