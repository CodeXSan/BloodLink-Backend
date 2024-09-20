package com.project.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("admins")
public class Admin {
	@MongoId
	private String id;
	@NotNull
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String firstName;
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String middleName;
	@NotNull
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String lastName;
	@NotNull
	@Email
	private String email;
	@NotNull
	@Pattern(regexp = "9\\d{9}", message = "Invalid phone number")
	private String phoneNumber;
	@NotNull
	private String address;
	@NotNull
	private String password;
	@Field("created_date")
	@CreatedDate
	private LocalDateTime createdDate;
	@Field("profile_updated_date")
	@LastModifiedDate
	private LocalDateTime profileUpdatedDate;

	 private Role role;
}
