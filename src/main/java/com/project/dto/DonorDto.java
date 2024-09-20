package com.project.dto;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import com.project.enums.BloodGroup;
import com.project.validation.ValidAge;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DonorDto {
	
	@NotNull(message = "First name is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String firstName;
	@Pattern(regexp = "^[a-zA-Z]*$", message = "Name must contain only letters")
	private String middleName;
	@NotNull(message = "Last name is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String lastName;
	@NotNull(message = "Phone number is required")
	@Pattern(regexp = "^9[0-9]{9}$", message = "Invalid phone number")
	private String phoneNumber;
	@NotNull(message = "Address is required")
	private String address;
	@NotNull(message = "Location coords is required")
	@GeoSpatialIndexed
	private double[] location;
	@NotNull(message = "Gender is required")
	private String gender;
	@NotNull(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;
	@NotNull(message = "DOB is required")
	 @Past(message = "Date of birth must be a past date")
	@ValidAge
	 private LocalDate dob;
	@NotNull(message = "Blood group is required")
	private BloodGroup bloodGroup;
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;
}
