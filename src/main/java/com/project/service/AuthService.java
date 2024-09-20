package com.project.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.dto.AdminDto;
import com.project.dto.DonorDto;
import com.project.enums.ERole;
import com.project.model.Admin;
import com.project.model.Donor;
import com.project.model.Role;
import com.project.repository.AdminRepository;
import com.project.repository.DonorRepository;
import com.project.repository.RoleRepository;

@Service
public class AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public Optional<Donor> findDonorByEmail(String email) {
        return donorRepository.findByEmail(email);
    }

    public Optional<Admin> findAdminByPhoneNumber(String phoneNumber) {
        return adminRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<Donor> findDonorByPhoneNumber(String phoneNumber) {
        return donorRepository.findByPhoneNumber(phoneNumber);
    }

    public Admin registerAdmin(AdminDto adminDto) {
    	if (adminRepository.findByEmail(adminDto.getEmail()).isPresent() ||
	            adminRepository.findByPhoneNumber(adminDto.getPhoneNumber()).isPresent()) {
	            throw new IllegalArgumentException("Admin with this email or phone number already exists");
	        }
        Admin admin = new Admin();
        admin.setFirstName(adminDto.getFirstName());
        admin.setMiddleName(adminDto.getMiddleName());
        admin.setLastName(adminDto.getLastName());
        admin.setEmail(adminDto.getEmail());
        admin.setPassword(encoder.encode(adminDto.getPassword()));
        admin.setAddress(adminDto.getAddress());
        admin.setPhoneNumber(adminDto.getPhoneNumber());
        admin.setCreatedDate(LocalDateTime.now());
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        admin.setRole(adminRole);
        Admin savedAdmin =  adminRepository.save(admin);
        return savedAdmin;
    }

    public Donor registerDonor(DonorDto donorDto) {
    	 if (donorRepository.findByEmail(donorDto.getEmail()).isPresent() ||
               donorRepository.findByPhoneNumber(donorDto.getPhoneNumber()).isPresent()) {
               throw new IllegalArgumentException("Donor with this email or phone number already exists");
           }
        Donor donor = new Donor();
        donor.setFirstName(donorDto.getFirstName());
        donor.setMiddleName(donorDto.getMiddleName());
        donor.setLastName(donorDto.getLastName());
        donor.setAddress(donorDto.getAddress());
        donor.setLocation(donorDto.getLocation());
        donor.setGender(donorDto.getGender());
        donor.setDob(donorDto.getDob());
        donor.setBloodGroup(donorDto.getBloodGroup());
        donor.setEmail(donorDto.getEmail());
        donor.setPassword(encoder.encode(donorDto.getPassword()));
        donor.setPhoneNumber(donorDto.getPhoneNumber());
        donor.setCreatedDate(LocalDateTime.now());
        
        Role donorRole = roleRepository.findByName(ERole.ROLE_DONOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        donor.setRole(donorRole);

        return donorRepository.save(donor);
    }
}
