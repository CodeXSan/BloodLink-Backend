package com.project.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import com.mongodb.lang.Nullable;
import com.project.enums.BloodGroup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "blood_requests")
public class BloodRequest {
    @MongoId
    private String id;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String patientFirstName;	
    @Nullable
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String patientLastName;
    @NotNull
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String attendeeFirstName;
    @NotNull
   	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
   	private String attendeeLastName;
    @NotNull
    @Pattern(regexp = "^977[0-9]{9}$", message = "Invalid phone number")
	private String phoneNumber;
    @DateTimeFormat
    private LocalDate dob;
    @NotNull
	private String address;
    @NotNull
    private double[] location;
	@NotNull
    private BloodGroup bloodGroup;
	@NotNull
	private double quantity;
    @Nullable
    private String note;
    @Field(name = "requested_date")
    @CreatedDate
    private LocalDateTime requestDate;
    @Field(name = "required_date")
    private LocalDate requiredDate;
    private boolean isCritical;
    private Donor acceptedBy;
    @Field(name = "donated_date")
    private LocalDate donatedDate;

    private String status;
    
    public void setLocation(double longitude, double latitude) {
        this.location = new double[] { longitude, latitude };
    }
}
