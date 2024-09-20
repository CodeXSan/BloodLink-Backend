package com.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.project.dto.AdminDto;
import com.project.dto.AdminResponse;
import com.project.dto.DonorDto;
import com.project.dto.DonorResponse;
import com.project.dto.ErrorResponse;
import com.project.dto.LoginDto;
import com.project.model.Admin;
import com.project.model.Donor;
import com.project.repository.AdminRepository;
import com.project.repository.DonorRepository;
import com.project.security.UserDetailsImpl;
import com.project.service.AuthService;
import com.project.service.DonorService;
import com.project.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DonorRepository donorRepository;
    @Autowired
    private DonorService donorService;

    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminDto adminDto) {
        try {
            Admin registeredAdmin = authService.registerAdmin(adminDto);
            return new ResponseEntity<>(registeredAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/admin/login")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginDto loginRequest) {
    	logger.info("Admin login attempt for email: {}", loginRequest.getIdentifier());
    	logger.info("password : {}", loginRequest.getPassword());
    	try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Admin admin = adminRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Error: Admin is not found."));

            return ResponseEntity.ok(new AdminResponse(jwt, 
                                 admin.getId(), 
                                 admin.getEmail(),
                                 admin.getRole(),
                                 admin.getFirstName(), 
                                 admin.getLastName(),
                                 admin.getMiddleName(),
                                 admin.getPhoneNumber(),
                                 admin.getAddress(),
                                 admin.getCreatedDate(),
                                 admin.getProfileUpdatedDate()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
      }


    @PostMapping("/donor/register")
    public ResponseEntity<?> registerDonor(@Valid @RequestBody DonorDto donorDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect error messages
            StringBuilder errorMsg = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMsg.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseEntity<>(new ErrorResponse(errorMsg.toString()), HttpStatus.BAD_REQUEST);
        }

        try {
            Donor registeredDonor = authService.registerDonor(donorDto);
            return new ResponseEntity<>(registeredDonor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/donor/login")
    public ResponseEntity<?> authenticateDonor(@Valid @RequestBody LoginDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        Donor donor = donorRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Donor is not found."));
            
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
    @GetMapping("/user-auth")
    public ResponseEntity<?> authenticateUser(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        if (jwtUtils.validateJwtToken(authToken)) {
            return ResponseEntity.ok("User authenticated");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        boolean tokenSent = donorService.generateAndSendToken(email);
        if (tokenSent) {
            return ResponseEntity.ok("Token sent to email.");
        } else {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }
    @PostMapping("/confirm-token")
    public ResponseEntity<String> confirmToken(@RequestParam String token) {
        boolean isValid = donorService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Token is valid.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                            @RequestParam("newPassword") String newPassword) {
        boolean passwordReset = donorService.resetPassword(token, newPassword);
        if (passwordReset) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid token.");
        }
    }
}
