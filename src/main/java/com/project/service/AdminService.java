package com.project.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.model.Admin;
import com.project.repository.AdminRepository;

@Service
public class AdminService {
	private AdminRepository adminRepository;
	public AdminService(AdminRepository adminRepository) {
		super();
		this.adminRepository = adminRepository;
	}
	
	 public Admin registerAdmin(Admin admin) {
	        if (adminRepository.findByEmail(admin.getEmail()).isPresent() ||
	            adminRepository.findByPhoneNumber(admin.getPhoneNumber()).isPresent()) {
	            throw new IllegalArgumentException("Admin with this email or phone number already exists");
	        }
	        return adminRepository.save(admin);
	    }

	    public Optional<Admin> findByEmail(String email) {
	        return adminRepository.findByEmail(email);
	    }

	    public Optional<Admin> findByPhoneNumber(String phoneNumber) {
	        return adminRepository.findByPhoneNumber(phoneNumber);
	    }
}
