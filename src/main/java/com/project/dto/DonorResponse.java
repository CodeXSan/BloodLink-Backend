package com.project.dto;

import com.project.enums.BloodGroup;
import com.project.model.Role;

import lombok.Data;

@Data
public class DonorResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private Role role;
    private String firstName;
    private String middleName;
    private String lastName;
	 private String address;
	 private double[] location;
	 private BloodGroup bloodGroup;
	public DonorResponse(String token, String id, Role role,String firstName,String middleName, String lastName, String address, double[] location, BloodGroup bloodGroup) {
		this.token = token;
		this.id = id;
		this.role = role;
		this.firstName=firstName;
		this.middleName=middleName;;
		this.lastName=lastName;
		this.address= address;
		this.location = location;
		this.bloodGroup = bloodGroup;
	}
}
