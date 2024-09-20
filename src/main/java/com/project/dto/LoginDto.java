package com.project.dto;

import lombok.Data;

@Data
public class LoginDto {
	 private String identifier; // This can be email or phone number
	 private String password;
}
