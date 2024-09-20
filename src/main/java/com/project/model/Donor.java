package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import com.mongodb.lang.Nullable;
import com.project.enums.BloodGroup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("donors")
public class Donor {
	@MongoId
	private String id;
	@NotNull(message = "First name is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String firstName;
	@Nullable
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String middleName;
	@NotNull(message = "Last name is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String lastName;
	@NotNull(message = "Phone number is required")
	@Pattern(regexp = "^\\9[0-9]{9}$", message = "Invalid phone number")
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
	 private LocalDate dob;
	@NotNull(message = "Blood group is required")
	private BloodGroup bloodGroup;
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;
	 private String profilePicture;
	 @DateTimeFormat
	 private LocalDate lastDonationDate;
	 @Field("created_date")
	 @CreatedDate
	 private LocalDateTime createdDate;
	 @Field("profile_updated_date")
	 @LastModifiedDate
	 private LocalDateTime profileUpdatedDate;
	 private String resetToken;
	 private Long tokenCreationTime;

	 private Role role;
	
	public void setLocation(double longitude, double latitude) {
        this.location = new double[] { longitude, latitude };
    }
	
	public void updateLastDonationDate() {
	    this.lastDonationDate = LocalDate.now();
	}
	
	public boolean canDonateAgain() {
        if (lastDonationDate == null) {
            return true;
        }
        LocalDate today = LocalDate.now();
        LocalDate eligibleDate = lastDonationDate.plusMonths(3);
        return today.isAfter(eligibleDate);
    }

}
