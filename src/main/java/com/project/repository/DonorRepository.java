package com.project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.enums.BloodGroup;
import com.project.model.Donor;

@Repository
public interface DonorRepository extends MongoRepository<Donor, String>{
	
	Optional<Donor> findByEmail(String email);
	Optional<Donor> findByPhoneNumber(String phoneNumber);
	@Query("{ 'location': { $nearSphere: { $geometry: { type: 'Point', coordinates: [?0, ?1] }, $maxDistance: ?2 } }, 'bloodGroup': ?3 }")
    List<Donor> findNearbyDonors(double longitude, double latitude, double maxDistance, BloodGroup bloodGroup);
	 @Query("{'$text': {'$search': ?0}}")
	    List<Donor> searchByText(String text);
	 @Query(value = "{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } }, { 'email': { '$regex': ?0, '$options': 'i' } }, { 'phoneNumber': { '$regex': ?0, '$options': 'i' } }, { 'gender': { '$regex': ?0, '$options': 'i' } }, { 'bloodGroup': { '$regex': ?0, '$options': 'i' } } ] }")
	    List<Donor> findBySearchQuery(String searchQuery);
	List<Donor> findByBloodGroup(BloodGroup bloodGroup);
	long countByCreatedDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
	Donor findByResetToken(String resetToken);
}
