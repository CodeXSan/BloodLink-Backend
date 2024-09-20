package com.project.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlogDto {
	@NotNull
	private String title;
	@NotNull
	private String content;
	private MultipartFile file;
}
