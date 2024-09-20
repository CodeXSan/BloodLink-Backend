package com.project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.exception.ResourceNotFoundException;
import com.project.model.BloodRequest;
import com.project.model.Donor;
import com.project.repository.BloodRequestRepository;
import com.project.repository.DonorRepository;
import com.project.security.UserDetailsServiceImpl;

import io.jsonwebtoken.lang.Arrays;

@Service
public class BloodRequestService {
	@Autowired
	private DonorRepository donorRepository;
	@Autowired
	private BloodRequestRepository bloodRequestRepository;
	
//	@Autowired
//	private TwilioService twilioService;
	@Autowired
	private EmailService emailService;
	
	public BloodRequest saveBloodRequest(BloodRequest bloodRequest) {
	    if (bloodRequest == null) {
	        throw new NullPointerException("Blood request cannot be null");
	    }

	    BloodRequest savedRequest = bloodRequestRepository.save(bloodRequest);
	    boolean isCritical = savedRequest.isCritical();
	    String message;

	    if (isCritical) {
	        message = "Critical blood request: "+savedRequest.getPatientFirstName()+" urgently needed " + savedRequest.getBloodGroup() + " blood at " + savedRequest.getAddress() + " by " + savedRequest.getRequiredDate()+".";
	        // Send SMS to all donors
	        List<Donor> allDonors = donorRepository.findAll();
	        for (Donor donor : allDonors) {
	            try {
//	                twilioService.sendSms(donor.getPhoneNumber(), message);
	                emailService.sendEmail(donor.getEmail(), "Critical Blood Request", message);
	            } catch (Exception e) {
	                System.err.println("Error sending SMS or Email to " + donor.getPhoneNumber() + ": " + e.getMessage());
	            }
	        }
	    } else {
	        message = "Urgent blood request: "+savedRequest.getPatientFirstName()+" urgently needed " + savedRequest.getBloodGroup() + " blood at " + savedRequest.getAddress() + " by " + savedRequest.getRequiredDate()+".";
	        // Send SMS to donors with the same blood group
	        List<Donor> donorsWithSameBloodGroup = donorRepository.findByBloodGroup(savedRequest.getBloodGroup());
	        for (Donor donor : donorsWithSameBloodGroup) {
	            try {
//	                twilioService.sendSms(donor.getPhoneNumber(), message);
	                emailService.sendEmail(donor.getEmail(), "Urgent Blood Request", message);
	            } catch (Exception e) {
	                System.err.println("Error sending SMS or Email to " + donor.getPhoneNumber() + ": " + e.getMessage());
	            }
	        }
	    }

	    return savedRequest;
	}
	
	public String acceptRequest(String requestId, String donorId) {
	    Optional<BloodRequest> optionalRequest = bloodRequestRepository.findById(requestId);
	    if (optionalRequest.isEmpty()) {
	        throw new ResourceNotFoundException("BloodRequest not found with id " + requestId);
	    }

	    Optional<Donor> optionalDonor = donorRepository.findById(donorId);
	    if (optionalDonor.isEmpty()) {
	        throw new ResourceNotFoundException("Donor not found with id " + donorId);
	    }

	    BloodRequest request = optionalRequest.get();
	    Donor donor = optionalDonor.get();

	    if ("Accepted".equals(request.getStatus()) || "Donated".equals(request.getStatus())) {
	        throw new IllegalArgumentException("This blood request has already been accepted or donated.");
	    }
	    if (!donor.getBloodGroup().equals(request.getBloodGroup())) {
	        throw new IllegalArgumentException("Donor's blood type does not match the requested blood type.");
	    }
	    if (!donor.canDonateAgain()) {
	        throw new IllegalArgumentException("You are not eligible to donate again.");
	    }
	    LocalDate currentDate = LocalDate.now();
        List<BloodRequest> existingAcceptedRequests = bloodRequestRepository.findByAcceptedByAndStatusInAndRequiredDateAfter(donor, List.of("Accepted", "Pending"), currentDate);
        if (!existingAcceptedRequests.isEmpty()) {
            throw new IllegalArgumentException("Donor has already accepted a pending non-expired request.");
        }
	    request.setStatus("Accepted");
	    request.setAcceptedBy(donor);
	    bloodRequestRepository.save(request);

	    return "Request Accepted";
	}

	    public BloodRequest updateToDonated(String requestId) {
	    	 BloodRequest request = bloodRequestRepository.findById(requestId)
	    	            .orElseThrow(() -> new ResourceNotFoundException("BloodRequest not found with id " + requestId));
	    	 Donor donor = request.getAcceptedBy();
	    	    if ("Donated".equals(request.getStatus())) {
	    	        throw new IllegalArgumentException("This blood request has already been marked as donated.");
	    	    }
	    	    if (!donor.getBloodGroup().equals(request.getBloodGroup())) {
		            throw new IllegalArgumentException("Your blood type does not match the requested blood type.");
		        }
	    	    request.setStatus("Donated");
	    	    request.setDonatedDate(LocalDate.now());
	    	    donor.updateLastDonationDate();
	    	    donorRepository.save(donor);
	    	    return bloodRequestRepository.save(request);
	    }
	    public void cancelRequest(String requestId) {
	        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
	            .orElseThrow(() -> new RuntimeException("Request not found"));

	        bloodRequest.setStatus("Pending");
	        bloodRequest.setAcceptedBy(null);
	        bloodRequestRepository.save(bloodRequest);
	    }
	    public List<BloodRequest> getDonationHistory(String donorId) {
	        List<BloodRequest> acceptedRequests = bloodRequestRepository.findByAcceptedByIdAndStatus(donorId, "Accepted");
	        List<BloodRequest> donatedRequests = bloodRequestRepository.findByAcceptedByIdAndStatus(donorId, "Donated");

	        // Combine both lists
	        List<BloodRequest> donationHistory = new ArrayList<>();
	        donationHistory.addAll(acceptedRequests);
	        donationHistory.addAll(donatedRequests);

	        return donationHistory;
	    }
	    
	    public List<BloodRequest> getAllPendingRequests() {
	    	LocalDate now = LocalDate.now();
	        return bloodRequestRepository.getAllPendingRequestsNotPast(now);
	    }

	    public List<BloodRequest> getAllAcceptedRequests() {
	        return bloodRequestRepository.getAllAcceptedRequestsNotPast(LocalDate.now());
	    }

	    public List<BloodRequest> getAllDonatedRequests() {
	        return bloodRequestRepository.findByStatus("Donated");
	    }

		public List<BloodRequest> getAllRequests() {
			return bloodRequestRepository.findAll();
		}
		 public List<BloodRequest> getPendingRequests() {
		        return bloodRequestRepository.findAll().stream()
		                .filter(request -> "Pending".equalsIgnoreCase(request.getStatus()))
		                .collect(Collectors.toList());
		    }

		    public List<BloodRequest> getAcceptedRequests() {
		        return bloodRequestRepository.findAll().stream()
		                .filter(request -> "Accepted".equalsIgnoreCase(request.getStatus()))
		                .collect(Collectors.toList());
		    }
		    
}
