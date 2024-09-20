package com.project.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.model.BloodRequest;
import com.project.model.Donor;

@Repository
public interface BloodRequestRepository extends MongoRepository<BloodRequest, String> {
	List<BloodRequest> findByStatus(String status);
	 @Query("{ 'status': 'Pending', 'required_date': { $gte: ?0 } }")
    List<BloodRequest> getAllPendingRequestsNotPast(LocalDate now);
	 @Query("{ 'status': 'Accepted', 'required_date': { $gte: ?0 } }")
    List<BloodRequest> getAllAcceptedRequestsNotPast(LocalDate now);
	long countByRequestDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
	List<BloodRequest> findByAcceptedByAndStatusInAndRequiredDateAfter(Donor donor, List<String> statuses, LocalDate currentDate);
	List<BloodRequest> findByAcceptedByIdAndStatus(String donorId, String status);
}
