package com.project.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
	private String firstName;
	private String middleName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String address;
	private double[] location;
}
