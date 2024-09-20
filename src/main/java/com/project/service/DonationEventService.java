package com.project.service;

import com.project.model.DonationEvent;
import com.project.model.Donor;
import com.project.repository.DonationEventRepository;
import com.project.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DonationEventService {

    @Autowired
    private DonationEventRepository eventRepository;

    @Autowired
    private DonorRepository donorRepository;

    public DonationEvent createEvent(DonationEvent event) {
        return eventRepository.save(event);
    }

    public Optional<DonationEvent> findById(String eventId) {
        return eventRepository.findById(eventId);
    }

    public void registerDonor(String eventId, String donorId) {
        Optional<DonationEvent> eventOpt = eventRepository.findById(eventId);
        Optional<Donor> donorOpt = donorRepository.findById(donorId);
        if (eventOpt.isPresent() && donorOpt.isPresent()) {
            DonationEvent event = eventOpt.get();
            event.getRegisteredDonorIds().add(donorId);
            eventRepository.save(event);
        }
    }
}
