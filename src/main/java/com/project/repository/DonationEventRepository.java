package com.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.model.DonationEvent;

public interface DonationEventRepository extends MongoRepository<DonationEvent, String>{

}
