package com.project.controller;

import com.project.model.Donor;
import com.project.repository.DonorRepository;
import com.project.security.UserDetailsImpl;
import com.project.service.DonorService;
import com.project.utils.JwtUtils;

import jakarta.validation.Valid;

import com.project.dto.DonorResponse;
import com.project.dto.ProfileUpdateRequest;
import com.project.enums.BloodGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/donor")
public class DonorController {
	@Autowired
	private DonorRepository donorRepository;

    @Autowired
    private DonorService donorService;
    @Autowired
    private JwtUtils jwtUtils;
    @Value("${upload.path}")
    private String uploadPath;
    private static final Logger logger = LoggerFactory.getLogger(DonorController.class);
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonorById(@PathVariable String id) {
        Optional<Donor> donor = donorService.findById(id);
        if (donor.isPresent()) {
            return new ResponseEntity<>(donor.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Donor not found", HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Donor> updateDonorProfile(@PathVariable("id") String donorId, @RequestBody Donor updatedDonor) {
        Donor donor = donorService.updateDonorProfile(donorId, updatedDonor);
        if (donor != null) {
            return ResponseEntity.ok(donor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonor(@PathVariable String id) {
        donorService.deleteDonor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/nearby/{bloodGroup}")
    public List<Donor> getNearbyDonors(@RequestParam double latitude, @RequestParam double longitude, @PathVariable String bloodGroup) {
    	BloodGroup bGroup = BloodGroup.fromString(bloodGroup);
        return donorService.findNearbyDonors(latitude, longitude, bGroup);
    }
    
    @PostMapping("/updateProfilePicture/{id}")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable("id") String donorId,
            @RequestParam("profilePicture") MultipartFile profilePictureFile
    ) {
        try {
            String photoUrl = donorService.updateProfilePicture(donorId, profilePictureFile);
            return ResponseEntity.ok(photoUrl);
        } catch (IOException e) {
            logger.error("Error uploading profile picture", e);
            return new ResponseEntity<>("Error uploading profile picture", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/profile-picture/{fileName}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String fileName) {
        File file = new File(uploadPath + fileName);
        if (file.exists()) {
            try {
                byte[] imageData = Files.readAllBytes(file.toPath());
                return ResponseEntity.ok().body(imageData);
            } catch (IOException e) {
                logger.error("Error reading file", e);
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateRequest profileUpdateRequest, Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Donor donor = donorService.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Donor not found with id: " + userPrincipal.getId()));
        if (profileUpdateRequest.getFirstName() != null) {
            donor.setFirstName(profileUpdateRequest.getFirstName());
        }
        if (profileUpdateRequest.getMiddleName() != null) {
            donor.setMiddleName(profileUpdateRequest.getMiddleName());
        }
        if (profileUpdateRequest.getLastName() != null) {
            donor.setLastName(profileUpdateRequest.getLastName());
        }
        if (profileUpdateRequest.getPhoneNumber() != null) {
            donor.setPhoneNumber(profileUpdateRequest.getPhoneNumber());
        }
        if (profileUpdateRequest.getAddress() != null) {
            donor.setAddress(profileUpdateRequest.getAddress());
        }
        if (profileUpdateRequest.getLocation() != null) {
            donor.setLocation(profileUpdateRequest.getLocation());
        }
        if (profileUpdateRequest.getEmail() != null) {
            donor.setEmail(profileUpdateRequest.getEmail());
        }
        donor.setProfileUpdatedDate(LocalDateTime.now());
        donorRepository.save(donor);
        Authentication updatedAuth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String jwt = jwtUtils.generateJwtToken(updatedAuth);
        return ResponseEntity.ok(new DonorResponse(jwt,
                donor.getId(),
                donor.getRole(),
                donor.getFirstName(),
                donor.getMiddleName(),
                donor.getLastName(),
                donor.getAddress(),
                donor.getLocation(),
                donor.getBloodGroup()));
    }
}
