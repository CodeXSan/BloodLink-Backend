package com.project.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.model.BloodRequest;
import com.project.service.BloodRequestService;

@CrossOrigin
@RestController
@RequestMapping("/blood")
public class BloodRequestController {
	private static final Logger logger = LoggerFactory.getLogger(BloodRequestController.class);
    @Autowired
    private BloodRequestService bloodRequestService;

    @PostMapping("/request")
    public ResponseEntity<?> saveBloodRequest(@RequestBody BloodRequest bloodRequest) {
        try {
        	bloodRequest.setRequestDate(LocalDateTime.now());
        	bloodRequest.setStatus("Pending");
            BloodRequest savedRequest = bloodRequestService.saveBloodRequest(bloodRequest);
            return ResponseEntity.ok(savedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while processing the request.");
        }
    }
    
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptRequest(@PathVariable("requestId") String requestId, @RequestParam String donorId) {
        try {
            String request = bloodRequestService.acceptRequest(requestId, donorId);
            return ResponseEntity.ok(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PutMapping("/mark-donated/{requestId}")
    public ResponseEntity<BloodRequest> markAsDonated(@PathVariable String requestId) {
        try {
            BloodRequest request = bloodRequestService.updateToDonated(requestId);
            return ResponseEntity.ok(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<BloodRequest>> getAllRequests() {
        try {
            List<BloodRequest> requests = bloodRequestService.getAllRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BloodRequest>> getAllPendingRequests() {
        try {
            List<BloodRequest> requests = bloodRequestService.getAllPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/accepted")
    public ResponseEntity<List<BloodRequest>> getAllAcceptedRequests() {
        try {
            List<BloodRequest> requests = bloodRequestService.getAllAcceptedRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/donated")
    public ResponseEntity<List<BloodRequest>> getAllDonatedRequests() {
        try {
            List<BloodRequest> requests = bloodRequestService.getAllDonatedRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PutMapping("/cancel/{requestId}")
    public ResponseEntity<?> cancelRequest(@PathVariable String requestId) {
        try {
            bloodRequestService.cancelRequest(requestId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/donationhistory/{donorId}")
    public ResponseEntity<List<BloodRequest>> getDonationHistory(@PathVariable("donorId") String donorId) {
        List<BloodRequest> donations = bloodRequestService.getDonationHistory(donorId);
        return ResponseEntity.ok(donations);
    }
}
