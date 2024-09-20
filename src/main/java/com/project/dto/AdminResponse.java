package com.project.dto;

import java.time.LocalDateTime;

import com.project.model.Role;

import lombok.Data;

@Data
public class AdminResponse {
	private String token;
	private String type = "Bearer";
	private String id;
	private String email;
	private Role role;
	private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String address;
	 private LocalDateTime createdDate;
	 private LocalDateTime profileUpdatedDate;
	public AdminResponse(String token, String id, String email, Role role, String firstName,
			String middleName, String lastName, String phoneNumber, String address, LocalDateTime createdDate, LocalDateTime profileUpdatedDate) {
		this.token = token;
		this.id = id;
		this.email = email;
		this.role = role;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.createdDate = createdDate;
		this.profileUpdatedDate = profileUpdatedDate;
	}
	
	
}
