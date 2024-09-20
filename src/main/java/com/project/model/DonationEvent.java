package com.project.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "events")
public class DonationEvent {

    @MongoId
    private String id;
    private String name;
    private LocalDateTime date;
    private String address;
    private List<String> registeredDonorIds;
}
