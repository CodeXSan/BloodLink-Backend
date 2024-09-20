package com.project.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.enums.BloodGroup;
import com.project.model.Donor;
import com.project.repository.DonorRepository;

@Service
public class DonorService {
	@Autowired
	private DonorRepository donorRepository;
	
	@Autowired
    private PictureUploadService pictureUploadService;
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final double MAX_DISTANCE_KM = 25.0;
	 private final long TOKEN_EXPIRY_TIME = TimeUnit.MINUTES.toMillis(15);
	
	 public Donor registerDonor(Donor donor) {
	        if (donorRepository.findByEmail(donor.getEmail()).isPresent() ||
	            donorRepository.findByPhoneNumber(donor.getPhoneNumber()).isPresent()) {
	            throw new IllegalArgumentException("User with this email or phone number already exists");
	        }
	        if (!isAgeValid(donor.getDob())) {
	            throw new IllegalArgumentException("Donor must be between 18 and 65 years old");
	        }
	        return donorRepository.save(donor);
	    }
	 	
	 public Optional<Donor> findById(String id){
		 return donorRepository.findById(id);
	 }
	 
	    public Optional<Donor> findByEmail(String email) {
	        return donorRepository.findByEmail(email);
	    }

	    public Optional<Donor> findByPhoneNumber(String phoneNumber) {
	        return donorRepository.findByPhoneNumber(phoneNumber);
	    }
	    
	    public Donor updateDonorProfile(String donorId, Donor updatedDonor){
	        Optional<Donor> donorOpt = donorRepository.findById(donorId);
	        if (donorOpt.isPresent()) {
	            Donor donor = donorOpt.get();
	            donor.setFirstName(updatedDonor.getFirstName());
	            donor.setMiddleName(updatedDonor.getMiddleName());
	            donor.setLastName(updatedDonor.getLastName());
	            donor.setEmail(updatedDonor.getEmail());
	            donor.setPhoneNumber(updatedDonor.getPhoneNumber());
	            donor.setDob(updatedDonor.getDob());
	            donor.setAddress(updatedDonor.getAddress());
	            donor.setLocation(updatedDonor.getLocation()[0], updatedDonor.getLocation()[1]);
	            donor.setProfileUpdatedDate(LocalDateTime.now());
	            return donorRepository.save(donor);
	        }
	        return null;
	    }

	    public void deleteDonor(String donorId) {
	        donorRepository.deleteById(donorId);
	    }
	    
	    public List<Donor> findNearbyDonors(double latitude, double longitude, BloodGroup bloodGroup) {
	        return donorRepository.findNearbyDonors(longitude, latitude, MAX_DISTANCE_KM*1000 , bloodGroup);
	    }
	    
	    public String updateProfilePicture(String donorId, MultipartFile profilePictureFile) throws IOException {
	        Optional<Donor> donorOpt = donorRepository.findById(donorId);
	        if (donorOpt.isPresent()) {
	            Donor donor = donorOpt.get();
	            String photoUrl = pictureUploadService.uploadProfilePicture(profilePictureFile);
	            donor.setProfilePicture(photoUrl);
	            donor.setProfileUpdatedDate(LocalDateTime.now());
	            donorRepository.save(donor);
	            return photoUrl;
	        } else {
	            throw new RuntimeException("Donor not found");
	        }
	    }
	    public List<Donor> searchDonors(String searchQuery) {
	        List<Donor> textSearchResults = donorRepository.searchByText(searchQuery);
	        List<Donor> regexSearchResults = donorRepository.findBySearchQuery(searchQuery);
	        Set<Donor> combinedResults = new HashSet<>(textSearchResults);
	        combinedResults.addAll(regexSearchResults);
	        return List.copyOf(combinedResults);
	    }
	    private boolean isAgeValid(LocalDate dob) {
	        if (dob == null) {
	            return false;
	        }
	        LocalDate today = LocalDate.now();
	        int age = Period.between(dob, today).getYears();
	        return age >= 18 && age <= 65;
	    }
	    
	    
	    public boolean generateAndSendToken(String email) {
	        Optional<Donor> donorOpt = donorRepository.findByEmail(email);
	        if (donorOpt == null) {
	            return false;
	        }
	        Donor donor = donorOpt.get();
	        String token = generateToken();
	        donor.setResetToken(token);
	        donor.setTokenCreationTime(System.currentTimeMillis());
	        donorRepository.save(donor);

	        emailService.sendResetToken(email, token);
	        return true;
	    }
	    
	    public boolean validateToken(String token) {
	        Donor donor = donorRepository.findByResetToken(token);
	        return donor != null && !isTokenExpired(donor.getTokenCreationTime());
	    }

	    public boolean resetPassword(String token, String newPassword) {
	        Donor donor = donorRepository.findByResetToken(token);
	        if (donor == null || isTokenExpired(donor.getTokenCreationTime())) {
	            return false;
	        }

	        donor.setPassword(passwordEncoder.encode(newPassword));
	        donor.setResetToken(null);
	        donor.setTokenCreationTime(null);
	        donorRepository.save(donor);
	        return true;
	    }

	    private String generateToken() {
	        Random random = new Random();
	        return String.format("%06d", random.nextInt(1000000));
	    }

	    private boolean isTokenExpired(long tokenCreationTime) {
	        return System.currentTimeMillis() - tokenCreationTime > TOKEN_EXPIRY_TIME;
	    }
}

