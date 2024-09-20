package com.project.model;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.Nullable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "blogs")
public class Blog {
	@Id
	private String id;
	@NotNull
    private String title;
	@NotNull
    private String content;
	@NotNull
    private String author;
    @Nullable
    private String photoUrl;
    @CreatedDate
    private LocalDate createdDate;
}
